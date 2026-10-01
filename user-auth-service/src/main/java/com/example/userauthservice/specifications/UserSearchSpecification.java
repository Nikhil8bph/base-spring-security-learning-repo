package com.example.userauthservice.specifications;

import com.example.userauthservice.entity.User;
import com.example.userauthservice.enums.UserIdentifier;
import org.springframework.data.jpa.domain.Specification;

import java.util.Calendar;

public final class UserSearchSpecification {

    public static Specification<User> getSpecification(String searchTerm,
                                                       UserIdentifier searchTypeEnum,
                                                       Calendar minCreatedAt,
                                                       Calendar maxCreatedAt,
                                                       Calendar minLastUpdatedAt,
                                                       Calendar maxLastUpdatedAt,
                                                       String createdBy,
                                                       String lastUpdatedBy,
                                                       Calendar minDeletedAt,
                                                       Calendar maxDeletedAt,
                                                       String deletedBy,
                                                       Boolean active,
                                                       Boolean deleted) {
        return Specification
                .where(hasSearchTerm(searchTerm, searchTypeEnum))
                .and(createdBetween(minCreatedAt, maxCreatedAt))
                .and(lastUpdatedBetween(minLastUpdatedAt, maxLastUpdatedAt))
                .and(hasCreatedBy(createdBy))
                .and(hasLastUpdatedBy(lastUpdatedBy))
                .and(deletedBetween(minDeletedAt, maxDeletedAt))
                .and(hasDeletedBy(deletedBy))
                .and(isActive(active))
                .and(isDeleted(deleted));
    }

    public static Specification<User> hasSearchTerm(String searchTerm, UserIdentifier searchTypeEnum) {
        return (root, query, cb) -> {
            if (searchTerm == null || searchTerm.trim().isEmpty()) {
                return cb.conjunction();
            }
            String pattern = "%" + searchTerm.trim().toLowerCase() + "%";
            if (searchTypeEnum == null) {
                return cb.or(
                        cb.like(cb.lower(root.get("email")), pattern),
                        cb.like(cb.lower(root.get("username")), pattern),
                        cb.like(cb.lower(root.get("mobile")), pattern)
                );
            }
            return switch (searchTypeEnum) {
                case ID -> cb.equal(root.get("id"), Long.parseLong(searchTerm));
                case EMAIL -> cb.like(cb.lower(root.get("email")), pattern);
                case USERNAME -> cb.like(cb.lower(root.get("username")), pattern);
                case MOBILE -> cb.like(cb.lower(root.get("mobile")), pattern);
            };
        };
    }

    public static Specification<User> hasSearchTerm(String searchTerm) {
        return hasSearchTerm(searchTerm, null);
    }

    public static Specification<User> createdBetween(Calendar minCreatedAt, Calendar maxCreatedAt) {
        return (root, query, cb) -> {
            if (minCreatedAt != null && maxCreatedAt != null) {
                return cb.between(root.get("createdDate"), minCreatedAt, maxCreatedAt);
            } else if (minCreatedAt != null) {
                return cb.greaterThanOrEqualTo(root.get("createdDate"), minCreatedAt);
            } else if (maxCreatedAt != null) {
                return cb.lessThanOrEqualTo(root.get("createdDate"), maxCreatedAt);
            }
            return cb.conjunction();
        };
    }

    public static Specification<User> lastUpdatedBetween(Calendar minLastUpdatedAt, Calendar maxLastUpdatedAt) {
        return (root, query, cb) -> {
            if (minLastUpdatedAt != null && maxLastUpdatedAt != null) {
                return cb.between(root.get("lastModifiedDate"), minLastUpdatedAt, maxLastUpdatedAt);
            } else if (minLastUpdatedAt != null) {
                return cb.greaterThanOrEqualTo(root.get("lastModifiedDate"), minLastUpdatedAt);
            } else if (maxLastUpdatedAt != null) {
                return cb.lessThanOrEqualTo(root.get("lastModifiedDate"), maxLastUpdatedAt);
            }
            return cb.conjunction();
        };
    }

    public static Specification<User> deletedBetween(Calendar minDeletedAt, Calendar maxDeletedAt) {
        return (root, query, cb) -> {
            if (minDeletedAt != null && maxDeletedAt != null) {
                return cb.between(root.get("deletedDate"), minDeletedAt, maxDeletedAt);
            } else if (minDeletedAt != null) {
                return cb.greaterThanOrEqualTo(root.get("deletedDate"), minDeletedAt);
            } else if (maxDeletedAt != null) {
                return cb.lessThanOrEqualTo(root.get("deletedDate"), maxDeletedAt);
            }
            return cb.conjunction();
        };
    }

    public static Specification<User> hasCreatedBy(String createdBy) {
        return (root, query, cb) ->
                createdBy == null || createdBy.trim().isEmpty()
                        ? cb.conjunction()
                        : cb.equal(root.get("createdBy"), createdBy.trim());
    }

    public static Specification<User> hasLastUpdatedBy(String lastUpdatedBy) {
        return (root, query, cb) ->
                lastUpdatedBy == null || lastUpdatedBy.trim().isEmpty()
                        ? cb.conjunction()
                        : cb.equal(root.get("lastModifiedBy"), lastUpdatedBy.trim());
    }

    public static Specification<User> hasDeletedBy(String deletedBy) {
        return (root, query, cb) ->
                deletedBy == null || deletedBy.trim().isEmpty()
                        ? cb.conjunction()
                        : cb.equal(root.get("deletedBy"), deletedBy.trim());
    }

    public static Specification<User> isActive(Boolean active) {
        return (root, query, cb) ->
                active == null ? cb.conjunction() : cb.equal(root.get("active"), active);
    }

    public static Specification<User> isDeleted(Boolean deleted) {
        return (root, query, cb) ->
                deleted == null ? cb.conjunction() : cb.equal(root.get("deleted"), deleted);
    }
}
