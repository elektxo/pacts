<#import "template.ftl" as layout>
<@layout.registrationLayout displayMessage=!messagesPerField.existsError('username','password') displayInfo=realm.password && realm.registrationAllowed && !registrationDisabled??; section>
    <#if section = "form">
        <div class="card text-center">
            <div class="card-body">
                <h5 class="card-title">${msg("loginAccountTitle")}</h5>

                <#if messagesPerField.existsError('username','password')>
                    <div class="alert alert-danger">
                        ${kcSanitize(messagesPerField.getFirstError('username','password'))?no_esc}
                    </div>
                </#if>
                
                <form class="mt-4" onsubmit="login.disabled = true; return true;" action="${url.loginAction}" method="post">
                    <!-- Username -->
                    <div class="form-floating">
                        <#if usernameEditDisabled??>
                        <input tabindex="1" type="text" class="form-control" id="username" name="username" value="${(login.username!'')}" disabled>
                        <#else>
                        <input tabindex="1" type="text" class="form-control" id="username" name="username" value="${(login.username!'')}" autofocus placeholder="username">
                        </#if>
                        <label for="username"><#if !realm.loginWithEmailAllowed>${msg("username")}<#elseif !realm.registrationEmailAsUsername>${msg("usernameOrEmail")}<#else>${msg("email")}</#if></label>
                    </div>

                    <!-- Pw -->
                    <div class="form-floating mt-3">
                        <input tabindex="2" type="password" class="form-control" id="password" name="password" value="${(login.username!'')}" autofocus placeholder="password">
                        <label for="password">${msg("password")}</label>
                    </div>

                    <!-- Forgot pw -->
                    <#if realm.resetPasswordAllowed>
                        <p class="float-end m-0 mt-3"><a tabindex="5" class="link-success" href="${url.loginResetCredentialsUrl}">${msg("doForgotPassword")}</a></p>
                    </#if>

                    <!-- Remember me -->
                    <#if realm.rememberMe && !usernameEditDisabled??>
                        <div class="form-check mt-3">
                            <#if login.rememberMe??>
                            <input class="form-check-input" type="checkbox" id="rememberMe" name="rememberMe" checked>
                            <#else>
                            <input class="form-check-input" type="checkbox" id="rememberMe" name="rememberMe">
                            </#if>
                            <label class="form-check-label" for="rememberMe">
                                ${msg("rememberMe")}
                            </label>
                        </div>
                    </#if>

                    <input type="hidden" id="id-hidden-input" name="credentialId" <#if auth.selectedCredential?has_content>value="${auth.selectedCredential}"</#if>/>
                    <button tabindex="4" type="submit" class="btn btn-success mt-3 mb-3 w-100">${msg("doLogIn")}</button>

                    <!-- Register -->
                    <#if realm.password && realm.registrationAllowed && !registrationDisabled??>
                    <span>Not a member? <a href="${url.registrationUrl}" class="link-success">${msg("doRegister")}</a></span>
                    </#if>
                </form>
            </div>
        </div>

        <!-- Social providers -->
        <#if realm.password && social.providers??>
            <div class="card  mt-3">
                <div class="card-body">
                    <h4 class="mb-3">${msg("identity-provider-login-label")}</h4>

                    <#list social.providers as p>
                        <a id="social-${p.alias}" class="btn btn-light w-100"
                                type="button" href="${p.loginUrl}">
                                ${p.displayName!}
                        </a>
                    </#list>
                </div>
            </div>
        </#if>
    </#if>

</@layout.registrationLayout>