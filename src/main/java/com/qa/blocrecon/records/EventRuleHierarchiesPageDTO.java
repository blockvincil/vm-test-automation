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
}
