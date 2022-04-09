# Authorization server OAuth2 - OpenID Connect

## Configuration discoer URLs

[http://localhost:9000/.well-known/openid-configuration](http://localhost:9000/.well-known/openid-configuration)

```
{
  "issuer":"http://localhost:9000",
  "authorization_endpoint":"http://localhost:9000/oauth2/authorize",
  "token_endpoint":"http://localhost:9000/oauth2/token",
  "token_endpoint_auth_methods_supported":["client_secret_basic","client_secret_post"],
  "jwks_uri":"http://localhost:9000/oauth2/jwks",
  "response_types_supported":["code"],
  "grant_types_supported":["authorization_code","client_credentials","refresh_token"],
  "subject_types_supported":["public"],
  "id_token_signing_alg_values_supported":["RS256"],
  "scopes_supported":["openid"]
}
```

## Test fase 1: autenticazione

1. andare su [https://oidcdebugger.com/](https://oidcdebugger.com/)
2. Authorize URI: [http://localhost:9000/oauth2/authorize](http://localhost:9000/oauth2/authorize)
3. Redirect URI: lascia il default
4. Client ID: unicam-turns-backend
5. State & Nonce: lascia il default
6. Response type: code
7. Response mode: post form
8. cliccare su [SEND REQUEST]
9. autenticarsi

## Test fase 2: ottenimento token

Se la fase 1 è andata a buon fine, oidcdebugger.com ci dovrebbe aver risposto con un 
`authorization_code`. Per testarlo si può utilizzare `curl` in questo modo, 
sostituendo ovviamente `<authorization_code>` con quello restituito da oidcdebugger.com:

```
curl http://localhost:9000/oauth2/token \
 -d "grant_type=authorization_code" \
 -d "client_id=unicam-turns-backend" \
 -d "client_secret=mimportassaitantosonosolounambientedisviluppo" \
 -d "redirect_uri=https%3A%2F%2Foidcdebugger.com%2Fdebug" \
 -d "code=<authorization_code>" \
 | python3 -m json.tool
```

Questa chiamata va fatta prima prima possibile perché `authorization_code` ha un  _expire time_
piuttosto breve. Se tutto va come previsto, si otterrà il JSON del _bearer token_, tipo il seguente:

```
{
    "access_token": "eyJraWQiOiJqd3RzaWduaW5nIiwiYWxnIjoiUlMyNTYifQ.eyJzdWIiOiJwaXBwbyIsImF1ZCI6InVuaWNhbS10dXJucy1iYWNrZW5kIiwibmJmIjoxNjQ5NTI5NDk1LCJzY29wZSI6WyJvcGVuaWQiXSwiaXNzIjoiaHR0cDpcL1wvbG9jYWxob3N0OjkwMDAiLCJleHAiOjE2NDk1Mjk3OTUsImlhdCI6MTY0OTUyOTQ5NX0.dwm2A7BrVjAjduW6JZ6cWqjowCXM6iGk4tUEWsIVAg2_K-Pd7SwFtDVhpxpjfhFfNIjjWjR2BqWkTVymcARoK-ozid06Gk_WLVjSopK0ww5XWmLA6z0mOrhH45clCK_dHIg8QuemiId8I-zkhw5kim9zsgPERvZ5oetvLeFWjWcDk7Myw46HcNVn0mT-oHwX3A1Gk38n0-k1dyBh4HqPX-t54UEmhAHpSczeTMfRg7G0ZiUlv5NARoYDIia0gpruhRGZ2TztgMytsLFhPidWyiaIlk18kgb0SoTW6AJGSsFBeEvC8bDXuu7KWChKAXsL9UAOfA-w4VWxhOPZctNsjw0Bu80_n7neJNO7fMV_wn0QBK9qSpywDfSEgetEwKuuDRKhUgQLtrsN_QqqHdIQVK4236Zqp8ope53nUuDgS3a7f8bA3ww4YUSz7ap-WMj3gxON43j728t0H9_dNCvUSo5ZWeyzV8osI8T-AaXfKtt0Z5FnqdIxyAOCY2lPttZYdDbKPfiTXxm-zLbg54uZ5UldpzWvWEYrKtHgSKWC73dSV5Mh0KY2SrMXI1ySzweMmLnIQ5D0wRylQLKuqnGklJk-usrM7oLS1erk-f_yG4uHvjNa_suT6blOyBy_Pr67LYNqciGrTQ_NElM1jkh1ru8bSJwfQyWRFITpYN5hMOE",
    "refresh_token": "t-8L7S1ZF3mMRf7jWPYIqULgRDnJ1mZk2-E77ao5SuxpMh81x44-ETS5SfDMBxeT1ATgtkrmQWWNqomtRJ-FtUnBWopPc6G3TPZO9e2shaydYsi-ces-pkNiPmftVyzQ",
    "scope": "openid",
    "id_token": "eyJraWQiOiJqd3RzaWduaW5nIiwiYWxnIjoiUlMyNTYifQ.eyJzdWIiOiJwaXBwbyIsImF1ZCI6InVuaWNhbS10dXJucy1iYWNrZW5kIiwiYXpwIjoidW5pY2FtLXR1cm5zLWJhY2tlbmQiLCJpc3MiOiJodHRwOlwvXC9sb2NhbGhvc3Q6OTAwMCIsImV4cCI6MTY0OTUzMTI5NSwiaWF0IjoxNjQ5NTI5NDk1LCJub25jZSI6IjFydzU1azM1ZDI2In0.E8XyD3sX0p5ssBWm4Z3wmIVWL5paby1CVYxyPXXnxZBmBQALkQlen9YajIM_bUBpjs-TQWzHXzu-3KkG94lz15KSXvZcFsAPLXuTjZ7JVuKGElgMX13GTeccAI6__uf5cSJcUM07_TGJfGickWFpbWX-gHGcco0dOUhZJaKS0Opf2rRFbFh7041w7FNTEUDCZgDkLQTlTOwrHsIYsqouH8f3VnEpHNK_naD7dLLm87Ql8kHeNoC3C53BrWPtg5yZ_IP_gS6ksyQPHJk1bJ0Z5APN0vpq6qx__W4ifks6xXEuVVRuniIU-BGEjbCjOomMMA6o87FVKHUm9PhhwkJEr7eDxdqm4YHMfXYN-E1wxFWJrS4MRhpy0du6eVBGOg3PxIahFgLDb9mJe-lBeSyzJFRcoNPkicEg65n9gDtPJQdK8GnMW2IZDzEkCtHHOj9K8dJy996i_qYEbZi2ZslT1xitfKct9s6lI4aaiwhr_igox7tm0Kw4xLCsGDWvyaKgh1pLpNnzYMnvmT3_gsRvtEJytBeZcEmqMa-OkkCkmB1-ptW5tT87rthZWbWwqBPdla7T8Y4wBuzZtpS0R9NlQA7La6vL9Qb8MiGi-dwfzhykGAMv9J8YRS-wIdjLhv13wLKeuJxTcDGKEWVVQ85Si8lM7cAtEx8qjB9syorpKCE",
    "token_type": "Bearer",
    "expires_in": 299
}
```
