package com.accionmfb.omnix.core.commons;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AuditAction {
    // Role Management
    ASSIGN_ROLE("Assign role to user"),
    CHANGE_PASSWORD("Change user password"),
    DELETE_ROLE("Delete role from system"),
    ADMIN_OVERVIEW("Admin overview/role(s)"),

    // User Management
    PLATFORM_USER_LOGIN("User login to platform"),
    PLATFORM_USER_LOGOUT("Platform user logout"),
    CREATE_PLATFORM_USER("Create new platform user"),
    CREATE_PLATFORM_ROLE("Create new platform role"),
    SET_ROLE_PERMISSIONS("Set permissions for role"),
    CREATE_ROLE_PERMISSION_MAPPING("Create role-permission mapping"),
    DELETE_PERMISSIONS("Delete permissions from role"),
    VIEW_USER_MANAGEMENT("View user management overview/details"),

    // Authentication & Security
    OTP_REQUEST("Request OTP for verification"),
    UPDATE_SYSTEM_CONFIGURATION("Update system configuration settings"),

    // Permission Management
    ADD_PERMISSIONS_TO_ROLE("Add permissions to role"),
    DEACTIVATE_PLATFORM_USER("Deactivate platform user account"),
    UPDATE_PLATFORM_USER("Update platform user information"),

    // Customer Management
    PLACE_PND_ON_CUSTOMER("Place Post No Debit restriction on customer"),
    APPROVE_CUSTOMER_UPGRADE("Approve customer account upgrade request"),
    REJECT_CUSTOMER_UPGRADE("Reject customer account upgrade request"),
    REMOVE_CUSTOMER_RESTRICTION("Remove restriction from customer account"),
    UPDATE_CUSTOMER_KYC_TIER("Update customer KYC tier level"),
    UPDATE_CUSTOMER_LIMITS("Update customer transaction limits"),
    VIEW_CUSTOMER("View customer information's/details"),

    // Account Management
    CREATE_ACCOUNT("Create new customer account"),
    UPDATE_ACCOUNT_STATUS("Update account status"),
    FREEZE_ACCOUNT("Freeze customer account"),
    UNFREEZE_ACCOUNT("Unfreeze customer account"),
    CLOSE_ACCOUNT("Close customer account"),
    DELETE_ACCOUNT_MAPPING("Delete account-to-merchant mapping"),
    VIEW_ACCOUNT("View account information/details"),

    // Transaction Management
    PROCESS_TRANSACTION("Process financial transaction"),
    REVERSE_TRANSACTION("Reverse financial transaction"),
    APPROVE_TRANSACTION("Approve pending transaction"),
    REJECT_TRANSACTION("Reject pending transaction"),
    REFUND_TRANSACTION("Process transaction refund"),
    VIEW_TRANSACTION("View transaction/details"),

    // Document Management
    UPLOAD_DOCUMENT("Upload customer document"),
    VERIFY_DOCUMENT("Verify customer document"),
    REJECT_DOCUMENT("Reject customer document"),
    DELETE_DOCUMENT("Delete customer document"),

    // System Operations
    SYSTEM_MAINTENANCE("Perform system maintenance"),
    DATA_BACKUP("Create system data backup"),
    DATA_RESTORE("Restore system data from backup"),
    CONFIGURATION_CHANGE("Change system configuration"),
    SECURITY_ALERT("Security alert triggered"),

    // Referral & Rewards
    PROCESS_REFERRAL_REWARD("Process referral reward payment"),
    APPROVE_REFERRAL_REWARD("Approve referral reward"),
    REJECT_REFERRAL_REWARD("Reject referral reward"),
    VIEW_REFERRAL("View referral/details"),

    // Notification Management
    SEND_NOTIFICATION("Send notification to user"),
    BULK_NOTIFICATION("Send bulk notification"),
    SCHEDULE_NOTIFICATION("Schedule notification for later delivery"),

    // Audit & Compliance
    AUDIT_LOG_ACCESS("Access audit logs"),
    COMPLIANCE_REPORT("Generate compliance report"),
    DATA_EXPORT("Export system data"),
    DATA_IMPORT("Import system data"),

    // Loan Management
    APPROVE_LOAN("Approve loan application"),
    REJECT_LOAN("Reject loan application"),
    DISBURSE_LOAN("Disburse approved loan"),
    PROCESS_LOAN_PAYMENT("Process loan payment"),
    REVERSE_LOAN_PAYMENT("Reverse loan payment"),
    TERMINATE_LOAN("Terminate loan agreement"),
    UPDATE_LOAN_STATUS("Update loan status"),
    REMOVE_CUSTOMER_FROM_BLACKLIST("Remove customer from loan blacklist"),
    PROCESS_LOAN_APPROVAL("Process loan approval or rejection"),
    PROCESS_SCHOOL_FEES_LOAN_APPROVAL("Process school fees loan approval or rejection"),
    PROCESS_BATCH_NIP_RETRY("Process batch NIP retry for loan payments"),
    VIEW_LOAN("View loan application/details"),

    // Savings Management
    TERMINATE_SAVINGS("Terminate customer savings plan"),
    CREATE_SAVINGS_PLAN("Create new savings plan"),
    UPDATE_SAVINGS_PLAN("Update savings plan details"),
    PROCESS_SAVINGS_TRANSACTION("Process savings transaction"),
    APPROVE_SAVINGS_WITHDRAWAL("Approve savings withdrawal request"),
    REJECT_SAVINGS_WITHDRAWAL("Reject savings withdrawal request"),
    VIEW_SAVINGS("View savings application/details"),

    // Merchant management
    UNLINK_ACCOUNT_WITH_MERCHANT("Unlink account with merchant")
    ;
    private final String description;
}
