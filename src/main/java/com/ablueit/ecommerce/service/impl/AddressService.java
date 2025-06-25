package com.ablueit.ecommerce.service.impl;


import com.ablueit.ecommerce.model.Address;
import com.ablueit.ecommerce.model.User;
import com.ablueit.ecommerce.repository.AddressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AddressService   {

    private final AddressRepository addressRepository;


    public Address saveAddress(Address address) {
        return addressRepository.save(address);
    }


    public List<Address> getAddressesByUser(User user) {
        return addressRepository.findByUser(user);
    }


    public Optional<Address> getAddressById(Long id) {
        return addressRepository.findById(id);
    }


    public void deleteAddress(Long id) {
        addressRepository.deleteById(id);
    }
}