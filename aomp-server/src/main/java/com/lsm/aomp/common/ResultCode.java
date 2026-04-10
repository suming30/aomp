package com.lsm.aomp.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResultCode {

    SUCCESS(200, "result.success"),
    CREATED(201, "result.created"),
    BAD_REQUEST(400, "result.bad.request"),
    UNAUTHORIZED(401, "result.unauthorized"),
    FORBIDDEN(403, "result.forbidden"),
    NOT_FOUND(404, "result.not.found"),
    METHOD_NOT_ALLOWED(405, "result.method.not.allowed"),
    CONFLICT(409, "result.conflict"),
    INTERNAL_ERROR(500, "result.internal.error"),

    USER_LOGIN_FAILED(1001, "result.user.login.failed"),
    USER_ACCOUNT_LOCKED(1002, "result.user.account.locked"),
    USER_ACCOUNT_DISABLED(1003, "result.user.account.disabled"),
    USER_PASSWORD_EXPIRED(1004, "result.user.password.expired"),
    USER_PASSWORD_FORCE_CHANGE(1005, "result.user.password.force.change"),
    USER_PASSWORD_MISMATCH(1006, "result.user.password.mismatch"),
    USER_PASSWORD_HISTORY(1007, "result.user.password.history"),
    USER_ACCOUNT_EXISTS(1008, "result.user.account.exists"),
    USER_EMAIL_EXISTS(1009, "result.user.email.exists"),
    TOKEN_EXPIRED(1010, "result.token.expired"),
    TOKEN_INVALID(1011, "result.token.invalid"),

    ROLE_NAME_EXISTS(1101, "result.role.name.exists"),
    ROLE_BUILTIN_CANNOT_DELETE(1102, "result.role.builtin.cannot.delete"),

    HOST_IP_EXISTS(1201, "result.host.ip.exists"),
    HOST_CONNECT_FAILED(1202, "result.host.connect.failed"),
    HOST_OFFLINE(1203, "result.host.offline"),

    SCRIPT_NAME_EXISTS(1301, "result.script.name.exists"),
    SCRIPT_DANGEROUS_COMMAND(1302, "result.script.dangerous.command"),
    SCRIPT_DRAFT_NOT_FOUND(1303, "result.script.draft.not.found"),

    TASK_ALREADY_RUNNING(1401, "result.task.already.running"),
    TASK_CANNOT_PAUSE(1402, "result.task.cannot.pause"),
    TASK_CANNOT_TERMINATE(1403, "result.task.cannot.terminate"),
    TASK_APPROVAL_REQUIRED(1404, "result.task.approval.required"),
    TASK_APPROVAL_TIMEOUT(1405, "result.task.approval.timeout"),
    TASK_APPROVAL_REJECTED(1406, "result.task.approval.rejected"),

    INSPECTION_RUNNING(1501, "result.inspection.running"),

    PERMISSION_DENIED(1601, "result.permission.denied"),
    TEMP_AUTH_EXPIRED(1602, "result.temp.auth.expired"),

    DANGEROUS_COMMAND_BLOCKED(1701, "result.dangerous.command.blocked"),
    DANGEROUS_COMMAND_NEED_APPROVAL(1702, "result.dangerous.command.need.approval"),
    DANGEROUS_COMMAND_WARNING(1703, "result.dangerous.command.warning");

    private final int code;
    private final String msg;
}
