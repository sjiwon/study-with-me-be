package com.kgu.studywithme.member.domain.repository.query.dto;

import com.kgu.studywithme.member.domain.model.Address;

public record AddressInfo(
        String province,
        String city
) {
    public AddressInfo(final Address address) {
        this(address.getProvince(), address.getCity());
    }
}
