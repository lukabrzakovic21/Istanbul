package com.master.istanbul.repository;

import com.master.istanbul.common.model.User;
import com.master.istanbul.common.util.UserStatus;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends CrudRepository<User, Integer> {

    Optional<User> findByPublicId(UUID publicId);
    Optional<User> findByEmail(String email);
    Optional<User> findByPublicIdAndStatusEquals(UUID publicId, UserStatus status);
    Iterable<User> findAllByStatusEquals(UserStatus status);

    @Query(
            value = "SELECT * FROM public.user u WHERE u.public_id in (?1)",
            nativeQuery = true)
    Collection<User> findAllByPublicId(List<UUID> ids);


}
