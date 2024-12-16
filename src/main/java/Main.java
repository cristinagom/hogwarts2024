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
         "(select h2.personByReceiver.id  from HousePoints h2 group by h2.personByReceiver.id " +
         "having  sum(h2.points) >= all (select sum(h.points) from HousePoints h group by h.personByReceiver.id))")
         */
        //Query2 : select p from Person as p where p.id =(select h2.personByReceiver.id  from HousePoints h2 group by h2.personByReceiver.id having  sum(h2.points) >= all (select sum(h.points) from HousePoints h group by h.personByReceiver.id))

        Query q2 = em.createNamedQuery("Person_MasPuntos", Person.class);
        Person p = (Person) q2.getSingleResult();
        System.out.println(p.toString());


        /*****
         *  3. Inventa consultas SQL (nativeQuery) que cumplan los siguientes requisitos:
         *  3.1. Una consulta con un innerjoin -- Ojo! No se puede hacer por culpa de que no coinciden los ids y se tienen que igualar manualmente. Sino, siempre con ON
         *      Consulta propuesta: listado de todas las casas con el total de ocupantes
         *  Query31: select h.name, count(p.id) from Person as p , House as h where p.houseByHouseId=h.id   group by h.id
         */


        Query consulta = em.createNativeQuery("select h.name, count(p.id) from person as p inner"+
                " join house as h  where p.house_id=h.id   group by h.id", Tuple.class);
        List<Tuple> tuplas = consulta.getResultList();

        System.out.println("Nombre de casa:"+"  Numero habitantes:");
        tuplas.forEach(tuple -> {
            System.out.println(tuple.get(0, String.class)+" ******* "+tuple.get(1,Long.class));
        });


        em.close();
        enm.close();
    }
}
