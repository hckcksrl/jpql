package jpql;

import javax.persistence.*;
import java.util.List;

public class JpaMain {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");

        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            Member member = new Member();
            member.setUsername("member1");
            member.setAge(10);
            member.setType(MemberType.ADMIN);
            em.persist(member);

            Member member2 = new Member();
            member2.setUsername("member1");
            member2.setAge(10);
            member2.setType(MemberType.USER);
            em.persist(member2);

            /**
             * getResultList => 결과가 하나 이상일때 리스트반환, 결과가 없을경우 빈 리스트 반환
             * getSingleResut => 결과가 무조건 하나여야함. 없을경우 NoResultException 발생, 둘 이상일 경우 NonUniqueResultException 발생
             * Spring Data JPA => 결과가 없으면 null또는 Optional 반환
             */
//            Member result = em.createQuery("select m from Member m where m.username = :username", Member.class)
//                    .setParameter("username", "member1")
//                    .getSingleResult();
//
//            System.out.println("result = " + result.getUsername());

            /**
             * "select m.team from Member m" 으로 조인문을 발생시킬수 있으나 명시적으로 조인문을 쓰는것이 좋다.
             * "select m from Member m join m.tea t" 명시적 조인문
             */
//            List<Team> resultList1 = em.createQuery("select m from Member m join m.tea t", Team.class)
//                    .getResultList();


            /**
             * 프로젝션 값을 가져올때 DTO를 사용해 가져오는것을 추천
             */
            List<MemberDTO> resultList = em.createQuery("select new jpql.MemberDTO(m.username, m.age) from Member m ", MemberDTO.class)
                    .getResultList();

            MemberDTO memberDTO = resultList.get(0);

            System.out.println("memberDTO = " + memberDTO.getUsername());
            System.out.println("memberDTO = " + memberDTO.getAge());

//            List<Member> resultList = query1.getResultList();
//
//            for (Member member1 : resultList) {
//                System.out.println("member1 = " + member1);
//            }

//            TypedQuery<String> query2 = em.createQuery("select m.username from Member m", String.class);
//            Query query3 = em.createQuery("select m.username, m.age from Member m");


            /**
             * JPA에서는 where, having 절에서만 서브쿼리 사용가능
             * select절에서도 가능 ( 하이버네이트에서 지원 )
             * from 절의 서브쿼리는 JPQL에서 불가능 ( 조인으로 풀수있으면 풀어서 해결해야함 )
             */

            List<Object[]> result = em.createQuery("select m.username, 'hello', true from Member m " +
                    "where m.type = jpql.MemberType.ADMIN")
                    .getResultList();

            for (Object[] objects : result) {
                System.out.println("objects = " + objects[0]);
                System.out.println("objects = " + objects[1]);
                System.out.println("objects = " + objects[2]);

            }

//            String query = "select function('group_concat', m.username) from Member m";
//
//            List<String> result2 = em.createQuery(query, String.class)
//                    .getResultList();
//
//            for (String s : result2) {
//                System.out.println("s = " + s);
//            }

            String query = "select m.team From Member m";

            List<String> result3 = em.createQuery(query, String.class)
                    .getResultList();


            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        } finally {
            em.close();
        }

    }
}

