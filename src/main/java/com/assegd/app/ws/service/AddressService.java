package com.assegd.app.ws.service;

import com.assegd.app.ws.shared.dto.AddressDTO;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;

public interface AddressService {
    List<AddressDTO> getAddresses(String userId);

    AddressDTO getAddress(String addressId);
}
