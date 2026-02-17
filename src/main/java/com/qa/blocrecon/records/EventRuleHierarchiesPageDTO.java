package com.qa.blocrecon.records;

import lombok.Data;

@Data
public class EventRuleHierarchiesPageDTO {
    private String importData;
    private String csvImport;
    private String purge;
    private String b1_openingClosingInconsistent;
    private String b1_balanceMissingOrInconsistent;
    private String b1_missingMandatoryFields;
    private String b1_accountMappingNotFound;
    private String b1_failedInTransformation;
    private String b2_openingClosingInconsistent;
    private String b2_balanceMissingOrInconsistent;
    private String b2_missingMandatoryFields;
    private String b2_accountMappingNotFound;
    private String b2_failedInTransformation;
}
