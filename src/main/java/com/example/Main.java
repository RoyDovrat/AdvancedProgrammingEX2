// package com.example;

// import org.hibernate.Session;
// import org.hibernate.SessionFactory;
// import org.hibernate.cfg.Configuration;


// public class Main {
//     public static void main(String[] args) {
//         // Build the Hibernate session factory
        
//         User user = new User();
//         user.setFirstName("Kermit");
//         user.setLastName("Frog");
//         user.setAge(54);
//         user.setEmail("kermit@muppets.com");
        
//         SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
//         System.out.println("here");
//         Session session = sessionFactory.openSession();
//         UserManager manager = new UserManager(session);
//         manager.saveUser(user);
//         System.out.println("User saved with ID = "+user.getUserId());
//     }
// }
