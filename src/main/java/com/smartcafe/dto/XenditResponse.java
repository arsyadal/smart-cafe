package com.smartcafe.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for Xendit Invoice Response
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class XenditResponse {
    private String invoiceUrl;
    private String externalId;
}
