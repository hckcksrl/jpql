package jpql;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.List;

public class JpaMain2 {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");

        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            Team teamA = new Team();
            teamA.setName("팀A");
            em.persist(teamA);

            Team teamB = new Team();
            teamB.setName("팀B");
            em.persist(teamB);

            Member member = new Member();
            member.setUsername("회원1");
            member.setTeam(teamA);
            em.persist(member);

            Member member2 = new Member();
            member2.setUsername("회원2");
            member2.setTeam(teamA);
            em.persist(member2);

            Member member3 = new Member();
            member3.setUsername("회원3");
            member3.setTeam(teamB);
            em.persist(member3);

            em.flush();
            em.clear();

            List<Member> resultList = em.createNamedQuery("Member.findByUsername", Member.class)
                    .setParameter("username", "회원1")
                    .getResultList();

            for (Member member1 : resultList) {
                System.out.println("member1 = " + member1);
            }

            String query = "select m from Member m join fetch m.team";

            List<Member> result = em.createQuery(query, Member.class)
                    .getResultList();

            for (Member member1 : result) {
                System.out.println("member1 = " + member1.getUsername() + ", " + member1.getTeam().getName());
            }

            /**
             * 페치 조인은 객체 그래프를 SQL 한번에 조회하는 개념
             * fetch join은 사용할때 조심해야한다. ( 둘 이상의 컬렉션은 페치 조인 할 수 없다. )
             * 컬렉션을 페치 조인하면 페이징 API(setFirstResult, setMaxResults)를 사용할 수 없다
             * 여러 테이블을 조인해서 엔티티가 가진 모양이 아닌 전혀 다른 결과를 내야 하면,
             * 페치 조인 보다는 일반 조인을 사용하고 필요한 데이터들만 조회해서 DTO로 반환하는 것이 효과적
             */
            String query2 = "select distinct t from Team t join fetch t.members";

            List<Team> result2 = em.createQuery(query2, Team.class)
                    .getResultList();

            for (Team team : result2) {
                System.out.println("team = " + team.getName() + "| members = " + team.getMembers().size());
            }

            /**
             * batch 를 써서 한꺼번에 가져오는방법
             */
            String query3 = "select t from Team t";

            List<Team> result3 = em.createQuery(query3, Team.class)
                    .getResultList();

            for (Team team : result3) {
                System.out.println("team = " + team.getName() + "| members = " + team.getMembers().size());
            }
            
            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        } finally {
            em.close();
        }

    }
}

