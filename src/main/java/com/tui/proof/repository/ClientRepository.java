package com.tui.proof.repository;

import com.tui.proof.model.Address;
import com.tui.proof.model.Client;
import com.tui.proof.model.PilotesOrder;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

@Repository
public interface ClientRepository extends JpaRepository<Client, Integer>, JpaSpecificationExecutor<Client> {

    @Query("SELECT A FROM Address A WHERE A.client.clientId = ?1")
    List<Address> findAllClientAddresses(Integer clientId);

    @Query("SELECT P FROM PilotesOrder P WHERE P.client.clientId = ?1")
    List<PilotesOrder> findAllClientOrders(Integer clientId);

    default List<Client> findByCriteria(String firstName, String lastName, String telephone) {
        return this.findAll(new Specification<Client>() {
            @Override
            public Predicate toPredicate(Root<Client> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<>();
                if (firstName != null) {
                    predicates.add(criteriaBuilder.or(criteriaBuilder.like(root.get("firstName"), "%" + firstName + "%")));
                }
                if (lastName != null) {
                    predicates.add(criteriaBuilder.or(criteriaBuilder.like(root.get("lastName"), "%" + lastName + "%")));
                }
                if (telephone != null) {
                    predicates.add(criteriaBuilder.or(criteriaBuilder.like(root.get("telephone"), "%" + telephone + "%")));
                }
                return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        });
    }


}
