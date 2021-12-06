package kamysh.repository;

import kamysh.entity.Chapter;
import kamysh.utils.InvalidValueException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import java.util.List;

public class ChapterRepositoryImpl implements ChapterRepository {

    private final SessionFactory sessionFactory;

    public ChapterRepositoryImpl() {
        this.sessionFactory = SessionFactoryBuilder.getSessionFactory();
    }

    @Override
    public List<Chapter> findAll() {
        Session session = sessionFactory.openSession();
        return session.createQuery("FROM CHAPTER").list();
    }

    @Override
    public void save(Chapter person) throws InvalidValueException {
        Session session = sessionFactory.openSession();

        Transaction transaction = session.beginTransaction();

        try {
            session.save(person);
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
        }
    }

    @Override
    public Chapter findById(Long id) {
        Session session = sessionFactory.openSession();
        return session.get(Chapter.class, id);
    }

    @Override
    public Chapter update(Chapter newValue) {
        Session session = sessionFactory.openSession();
        session.update(newValue);
        return newValue;
    }

    @Override
    public void delete(Long id) {
        Session session = sessionFactory.openSession();

        Transaction transaction = session.beginTransaction();

        try {
            session.createQuery("delete from kamysh.entity.Chapter where id=:id").setParameter("id", id).executeUpdate();
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
        }
    }
}
