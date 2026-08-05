package com.europay.hub.features.merchant.application;

import com.europay.hub.features.merchant.application.dto.MerchantResponse;
import com.europay.hub.features.merchant.domain.MerchantRepository;
import com.europay.hub.shared.exception.ResourceNotFoundException;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MerchantQueryService {

    private final MerchantRepository merchantRepository;

    public MerchantQueryService(MerchantRepository merchantRepository) {
        this.merchantRepository = merchantRepository;
    }

    @Transactional(readOnly = true)
    public MerchantResponse getById(UUID merchantId) {
        return merchantRepository.findById(merchantId)
                .map(MerchantResponse::from)
                .orElseThrow(() -> new ResourceNotFoundException("Merchant", merchantId));
    }
}
