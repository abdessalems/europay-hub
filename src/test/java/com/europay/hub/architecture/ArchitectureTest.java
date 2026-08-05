package com.europay.hub.architecture;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

/**
 * Executable guardrails for the Clean Architecture dependency rule. These run in CI, so
 * a violation of the layering breaks the build — the boundaries are enforced, not just documented.
 */
@AnalyzeClasses(packages = "com.europay.hub", importOptions = ImportOption.DoNotIncludeTests.class)
class ArchitectureTest {

    @ArchTest
    static final ArchRule domain_must_not_depend_on_spring =
            noClasses().that().resideInAPackage("..domain..")
                    .should().dependOnClassesThat().resideInAnyPackage("org.springframework..");

    @ArchTest
    static final ArchRule domain_must_not_depend_on_persistence_framework =
            noClasses().that().resideInAPackage("..domain..")
                    .should().dependOnClassesThat().resideInAnyPackage("jakarta.persistence..");

    @ArchTest
    static final ArchRule domain_must_not_depend_on_web =
            noClasses().that().resideInAPackage("..domain..")
                    .should().dependOnClassesThat().resideInAnyPackage("..presentation..", "jakarta.servlet..");

    @ArchTest
    static final ArchRule presentation_must_not_depend_on_infrastructure =
            noClasses().that().resideInAPackage("..presentation..")
                    .should().dependOnClassesThat().resideInAPackage("..infrastructure..");
}
