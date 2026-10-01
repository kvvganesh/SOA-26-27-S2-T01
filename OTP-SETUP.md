# OTP delivery - setup checklist

## 1. Add the mail dependency to your pom.xml  (your zip did not include it)
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-mail</artifactId>
</dependency>
```

## 2. Give existing users an e-mail (new registrations ask for it)
```sql
UPDATE app_users SET email = 'you@gmail.com' WHERE username = 'ganesh';
```
(`ddl-auto=update` adds the `email` column on first start. Table name may differ - check yours.)

## 3. Choose how OTPs are delivered

| Mode | How to enable |
|------|---------------|
| **Real e-mail (Gmail)** | Turn on 2-step verification, create a Google *App Password*, then set env vars `MAIL_USERNAME=you@gmail.com` and `MAIL_PASSWORD=<16-char app password>` |
| **Local fake inbox (MailHog) - best for testing** | `docker run -p 1025:1025 -p 8025:8025 mailhog/mailhog`, set env vars `MAIL_HOST=localhost MAIL_PORT=1025 MAIL_SMTP_AUTH=false MAIL_STARTTLS=false`, read mail at http://localhost:8025 |
| **Console only (development)** | `SPRING_PROFILES_ACTIVE=dev` - OTP is printed in the backend console like before |

Set env vars in IntelliJ: Run > Edit Configurations > Environment variables.

## 4. Test it
1. Register a new user with an e-mail address.
2. Log in from an untrusted device -> OTP screen says "sent to g***@gmail.com" -> e-mail arrives -> enter the code.
3. "Forgot password" -> reset e-mail arrives.
4. Stop MailHog / use a wrong Gmail password -> login shows "We couldn't send the code right now" (HTTP 503) instead of a 500.
5. A user with no e-mail on file -> clear message (HTTP 422).

## Troubleshooting
- `AuthenticationFailedException` in the backend log: wrong/expired App Password, or you used your normal Gmail password.
- Nothing arrives: check spam; confirm port 587 isn't blocked by your network.
- App won't start: make sure the `spring-boot-starter-mail` dependency is in pom.xml.
