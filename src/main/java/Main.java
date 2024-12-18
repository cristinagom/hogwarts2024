import entities.Person;
import jakarta.persistence.*;

import java.math.BigInteger;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        EntityManagerFactory enm = Persistence.createEntityManagerFactory("default");
        EntityManager em = enm.createEntityManager();

        /*****
         *  1. Crea una consulta parametrizada con JPQL que permita encontrar
         *  todos los personajes que han sido alumnos de un maestro facilitado por parámetro.
         */
        //Query1: select p from Person p join p.courses c where c.teacher.firstName ='Minerva'
        Query q1 = em.createQuery("select p from Person p join Enrollment e on p=e.personEnrollment and e.courseEnrollment in (select c from Course c where c.teacher.firstName = :name)");
        //Query q1 = em.createQuery("select p from Person p join p.courses c where c.teacher.firstName = :name ");
        String nombre = "Minerva";
        q1.setParameter("name", nombre);
        System.out.println("Listado de alumn@s de la profesora: " + nombre);
        List<Person> list = q1.getResultList();
        for (Person p : list) {
            System.out.println("Alumn@:" + p.toString());
        }
        System.out.println("Listado de alumn@s de la profesora: " + nombre);
        list.forEach(person -> {
            System.out.println(person.toString());
        });

        /*****
         *  2. Crea dos consultas nominales (namedQuery) con sintaxis JPSQL
         *  que permita conocer el personaje que ha recibido más puntos y el
         *  que ha entregado más puntos a otro personajes
         *
         *  Dentro de la clase Person indicar:
         *
         @NamedQuery(name="Person_MasPuntos",query="select p  from Person as p where p.id =" +
         "(select h2.receiver.id  from HousePoints h2 group by h2.receiver.id " +
         "having  sum(h2.points) >= all (select sum(h.points) from HousePoints h group by h.receiver.id))")
         */
        //Query2 : select p from Person as p where p.id =(select h2.receiver.id  from HousePoints h2 group by h2.receiver.id having  sum(h2.points) >= all (select sum(h.points) from HousePoints h group by h.receiver.id))

        Query q2 = em.createNamedQuery("Person_MasPuntos", Person.class);
        Person p = (Person) q2.getSingleResult();
        System.out.println(p.toString());


        /*****
         *  3. Inventa consultas SQL (nativeQuery) que cumplan los siguientes requisitos:
         *  3.1. Una consulta con un innerjoin -- Ojo! No se puede hacer por culpa de que no coinciden los ids y se tienen que igualar manualmente. Sino, siempre con ON
         *      Consulta propuesta: listado de todas las casas con el total de ocupantes
         *  Query31: select h.name, count(p.id) from Person as p , House as h where p.house.id=h.id   group by h.id
         */


        Query consulta = em.createNativeQuery("select h.name, count(p.id) from person as p inner"+
                " join house as h where p.house_id=h.id   group by h.id", Tuple.class);
        List<Tuple> tuplas = consulta.getResultList();

        System.out.println("Nombre de casa:"+"  Numero habitantes:");
        tuplas.forEach(tuple -> {
            System.out.println(tuple.get(0, String.class)+" ******* "+tuple.get(1,Long.class));
        });

        /*****
         *  3. Inventa consultas SQL (nativeQuery) que cumplan los siguientes requisitos:
         *  3.2. Una consulta con un outerjoin
         *      Consulta propuesta: listar el nombre de los cursos con el nombre de su profesor
         * Query32 (NATIVE): select  c.name,p.first_name  from course as c left join  person as p on c.teacher_id=p.id;
         */

        Query consulta3 = em.createNativeQuery("select  c.name,p.first_name  from course as c" +
                " left join  person as p on c.teacher_id=p.id;", Tuple.class);
        List<Tuple> tuplas2 = consulta3.getResultList();

        System.out.println("Nombre del curso:"+"  Nombre del profesor:");
        tuplas2.forEach(tuple -> {

            System.out.println(tuple.get(0, String.class)+" ******* "+tuple.get(1, String.class));

        });


        /*****
         *  3. Inventa consultas SQL (nativeQuery) que cumplan los siguientes requisitos:
         *  3.3. Una consulta con una subconsulta (que no se puede codificar con un JOIN en la consulta externa)
         *      Consulta propuesta: listar las casas con la cantidad de diferentes cursos en los que se encuentran
         *      implicados mediante los alumnos
         *      Consulta SQL :  select h.name,
         *                      ( select count(e.person_enrollment) from enrollment as e
         * 	                        where e.person_enrollment in (select p.id from person as p
         * 							where p.house_id=h.id)) as cantidad_cursos
         * 								from house as h;
         *
         *
         */

        Query consulta4 = em.createNativeQuery("""
                select 
                h.name,(select count(e.person_enrollment) from enrollment as e
                where e.person_enrollment in (select p.id from person as p 
                where p.house_id=h.id)) as cantidad_cursos
                from house as h """, Tuple.class);
        List<Tuple> tuplas3 = consulta4.getResultList();

        System.out.println("Nombre del curso:"+"  Cantidad de cursos implicados:");
        tuplas3.forEach(tuple -> {

            System.out.println(tuple.get(0, String.class)+" ******* "+tuple.get(1, Long.class));

        });


        /*****
         *  EXTRA1: Realiza el borrado de un personaje concreto a través de una Query.
         *
         */

        Query delte = em.createNativeQuery("delete from person as p where  p.first_name like 'Euan'");
        //int resultado = delte.executeUpdate();
        //System.out.println("Resultado de la operación de borrado: "+ resultado);


        /*****
         *   EXTRA2: Busca ejemplos de la notación HQL y demuestra su uso en la base de datos hogwarts
         *
         *
         *
         */


        Query qry5 = em.createQuery( "select c.teacher.firstName from Course c", Tuple.class);

        List<Tuple> list4 = qry5.getResultList();

        list4.forEach(list1 -> {
            System.out.println(list1.get(0, String.class));

        });

        Query qry6 = em.createQuery( "select p.firstName, p.house.name from Person p where p.id=:id", Tuple.class);
        qry6.setParameter("id",12);

        List<Tuple> list6 = qry6.getResultList();

        list6.forEach(list2 -> {
            System.out.println(list2.get(0, String.class)+" ******* "+ list2.get(1,String.class));

        });



        em.close();
        enm.close();
    }
}
