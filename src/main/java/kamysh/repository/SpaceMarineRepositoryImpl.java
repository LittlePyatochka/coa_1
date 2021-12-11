package kamysh.repository;

import kamysh.controller.FilterConfiguration;
import kamysh.entity.AstartesCategory;
import kamysh.entity.SpaceMarine;
import kamysh.filters.SpaceMarineFilter;
import kamysh.utils.Error;
import kamysh.utils.InvalidValueException;
import kamysh.utils.MissingEntityException;
import kamysh.utils.Utils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import javax.persistence.criteria.*;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SpaceMarineRepositoryImpl implements SpaceMarineRepository {

    private final SessionFactory sessionFactory;

    public SpaceMarineRepositoryImpl() {
        this.sessionFactory = SessionFactoryBuilder.getSessionFactory();
    }

    @Override
    public List<SpaceMarine> findAll() {
        Session session = sessionFactory.openSession();
        return session.createQuery("FROM SPACE_MARINE").list();
    }

    @Override
    public List<SpaceMarine> findAll(FilterConfiguration filterConfiguration) throws ParseException, InvalidValueException {
        Session session = sessionFactory.openSession();
        List<SpaceMarine> result;

        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
        CriteriaQuery<SpaceMarine> criteria = criteriaBuilder.createQuery(SpaceMarine.class);
        Root<SpaceMarine> root = criteria.from(SpaceMarine.class);

        if (filterConfiguration.getOrder() != null) {
            List<Order> orders = new ArrayList<>();

            for (String order : filterConfiguration.getOrder()) {
                String[] parts = order.split(",");
                if (parts.length == 2) {
                    if (parts[1].equals("d")) {
                        orders.add(criteriaBuilder.desc(root.get(parts[0])));
                    } else if (parts[1].equals("a")) {
                        orders.add(criteriaBuilder.asc(root.get(parts[0])));
                    }
                }
            }

            criteria.orderBy(orders);
        }

        if (filterConfiguration.getFilter() != null) {
            List<Predicate> predicates = new ArrayList<>();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

            for (String filter : filterConfiguration.getFilter()) {
                String[] parts = filter.split(",");

                if (parts.length == 3) {
                    switch (parts[1]) {
                        case "==":
                            if (parts[0].equals("creationDate")) {
                                predicates.add(criteriaBuilder.equal(root.<Date>get(parts[0]), formatter.parse(parts[2])));
                            } else if (parts[0].equals("coordinates") || parts[0].equals("chapter")) {
                                if (parts[0].equals("chapter") && parts[2].equals("None")) {
                                    predicates.add(criteriaBuilder.isNull(root.get(parts[0]).get("id")));
                                } else {
                                    predicates.add(criteriaBuilder.equal(root.get(parts[0]).get("id"), parts[2]));
                                }
                            } else if (parts[0].equals("category")) {
                                predicates.add(criteriaBuilder.equal(root.get(parts[0]), AstartesCategory.valueOf(parts[2])));
                            } else if (parts[0].equals("loyal")) {
                                predicates.add(criteriaBuilder.equal(root.get(parts[0]), Boolean.valueOf(parts[2])));
                            } else if (parts[0].equals("heartCount") || parts[0].equals("health") || parts[0].equals("id")) {
                                predicates.add(criteriaBuilder.equal(root.get(parts[0]), Integer.valueOf(parts[2])));
                            } else {
                                predicates.add(criteriaBuilder.equal(root.get(parts[0]), parts[2]));
                            }

                            break;
//                        case "<=":
//                            if (parts[0].equals("creationDate")) {
//                                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get(parts[0]), formatter.parse(parts[2])));
//                            } else {
//                                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get(parts[0]), parts[2]));
//                            }
//                            break;
//                        case ">=":
//                            if (parts[0].equals("creationDate")) {
//                                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get(parts[0]), formatter.parse(parts[2])));
//                            } else {
//                                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get(parts[0]), parts[2]));
//                            }
//                            break;
//                        case "<":
//                            if (parts[0].equals("creationDate")) {
//                                predicates.add(criteriaBuilder.lessThan(root.get(parts[0]), formatter.parse(parts[2])));
//                            } else {
//                                predicates.add(criteriaBuilder.lessThan(root.get(parts[0]), parts[2]));
//                            }
//                            break;
//                        case ">":
//                            if (parts[0].equals("creationDate")) {
//                                predicates.add(criteriaBuilder.greaterThan(root.get(parts[0]), formatter.parse(parts[2])));
//                            } else {
//                                predicates.add(criteriaBuilder.greaterThan(root.get(parts[0]), parts[2]));
//                            }
//                            break;
                        case "contains":
                            predicates.add(criteriaBuilder.like(root.get(parts[0]), "%" + parts[2] + "%"));
                            break;
                        default:
                    }
                }
            }

            criteria.where(criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()])));
        }

        Query<SpaceMarine> query = session.createQuery(criteria);

        if(filterConfiguration.getCount() != null && filterConfiguration.getPage() == null){
            query.setMaxResults(filterConfiguration.getCount());
            query.setFirstResult(filterConfiguration.getCount() - 1);
        }

        if (filterConfiguration.getCount() != null && filterConfiguration.getPage() != null) {
            query.setMaxResults(filterConfiguration.getCount());
            query.setFirstResult(filterConfiguration.getCount() * (filterConfiguration.getPage() - 1));
        }

        result = query.list();

        return result;
    }

    @Override
    public void save(SpaceMarine spaceMarine) {
        Session session = sessionFactory.openSession();

        Transaction transaction = session.beginTransaction();

        try {
            session.save(spaceMarine);
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
        }
    }

    @Override
    public SpaceMarine findById(Long id) {
        Session session = sessionFactory.openSession();
        return session.get(SpaceMarine.class, id);
    }

    @Override
    public SpaceMarine update(SpaceMarine newValue) {
        Session session = sessionFactory.openSession();

        Transaction transaction = session.beginTransaction();

        try {
            session.update(newValue);
            transaction.commit();
            return newValue;
        } catch (Exception e) {
            transaction.rollback();
            return null;
        }
    }

    @Override
    public void delete(Long id) {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();

        try {
            session.createQuery("delete from kamysh.entity.SpaceMarine where id=:id").setParameter("id", id).executeUpdate();
            transaction.commit();
//            return true;
        } catch (Exception e) {
            transaction.rollback();
//            return false;
        }
    }
}
