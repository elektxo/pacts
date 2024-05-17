<#import "template.ftl" as layout>
<@layout.registrationLayout
  displayMessage=!messagesPerField.existsError("firstName", "lastName", "email", "username", "password", "password-confirm");
  section
>
  <#if section="header">
    <div class="card-header">${msg("registerTitle")}</div>
  <#elseif section="form">
<div class="card text-center">
    <div class="card-body">
      <form action="${url.registrationAction}" method="post">
        <div class="mb-3 form-floating">
          <input type="text" class="form-control" id="firstName" name="firstName" value="${(register.formData.firstName!'')}" autofocus required>
          <label for="firstName">${msg("firstName")}</label>
          <div class="invalid-feedback">${kcSanitize(messagesPerField.get("firstName"))}</div>
        </div>
        <div class="mb-3 form-floating">
          <input type="text" class="form-control" id="lastName" name="lastName" value="${(register.formData.lastName!'')}" required>
          <label for="lastName">${msg("lastName")}</label>
          <div class="invalid-feedback">${kcSanitize(messagesPerField.get("lastName"))}</div>
        </div>
        <div class="mb-3 form-floating">
          <input type="email" class="form-control" id="email" name="email" value="${(register.formData.email!'')}" required>
          <label for="email">${msg("email")}</label>
          <div class="invalid-feedback">${kcSanitize(messagesPerField.get("email"))}</div>
        </div>
        <#if !realm.registrationEmailAsUsername>
          <div class="mb-3 form-floating">
            <input type="text" class="form-control" id="username" name="username" value="${(register.formData.username!'')}" required>
            <label for="username">${msg("username")}</label>
            <div class="invalid-feedback">${kcSanitize(messagesPerField.get("username"))}</div>
          </div>
        </#if>
        <#if passwordRequired??>
          <div class="mb-3 form-floating">
            <input type="password" class="form-control" id="password" name="password" required>
            <label for="password">${msg("password")}</label>
            <div class="invalid-feedback">${kcSanitize(messagesPerField.getFirstError("password", "password-confirm"))}</div>
          </div>
          <div class="mb-3 form-floating">
            <input type="password" class="form-control" id="password-confirm" name="password-confirm" required>
            <label for="password-confirm">${msg("passwordConfirm")}</label>
            <div class="invalid-feedback">${kcSanitize(messagesPerField.get("password-confirm"))}</div>
          </div>
        </#if>
        <#if recaptchaRequired??>
          <div class="mb-3">
            <div class="g-recaptcha" data-sitekey="${recaptchaSiteKey}" data-size="compact"></div>
          </div>
        </#if>
        <button type="submit" class="btn btn-success mt-3 mb-3 w-100">${msg("doRegister")}</button>
      </form>
    </div>
</div>
    <div class="card-footer text-muted">
      <a href="${url.loginUrl}" class="link-success">${kcSanitize(msg("backToLogin"))}</a>
    </div>
  </#if>
</@layout.registrationLayout>
