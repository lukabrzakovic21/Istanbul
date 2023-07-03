package com.master.istanbul.repository;

import com.master.istanbul.common.model.RegistrationRequest;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RegistrationRequestRepository extends CrudRepository<RegistrationRequest, Integer> {

    Optional<RegistrationRequest> findByPublicId(UUID publicId);

    Optional<RegistrationRequest> findByEmail(String email);

}
