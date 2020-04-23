package com.assegd.app.ws.io.repositories;

import com.assegd.app.ws.io.entity.AddressEntity;
import com.assegd.app.ws.io.entity.UserEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AddressRepository extends CrudRepository<AddressEntity, Long> {

    Iterable<AddressEntity> findAllByUserDetails(UserEntity userEntity);
    AddressEntity findByAddressId(String addressId);
}
