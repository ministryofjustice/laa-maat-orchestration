{{/* vim: set filetype=mustache: */}}
{{/*
Environment variables for service containers
*/}}
{{- define "laa-maat-orchestration.env-vars" }}
env:
  - name: AWS_REGION
    value: {{ .Values.aws_region }}
  - name: SENTRY_DSN
    valueFrom:
        secretKeyRef:
            name: sentry-dsn
            key: SENTRY_DSN
  - name: SENTRY_ENV
    value: {{ .Values.java.host_env }}
  - name: SENTRY_SAMPLE_RATE
    value: {{ .Values.sentry.sampleRate | quote }}
  - name: LOG_LEVEL
    value: {{ .Values.logging.level }}
  - name: MAAT_API_BASE_URL
    value: {{ .Values.maatApi.baseUrl }}
  - name: MAAT_API_OAUTH_URL
    value: {{ .Values.maatApi.oauthUrl }}
  - name: HARDSHIP_API_BASE_URL
    value: {{ .Values.hardshipApi.baseUrl }}
  - name: HARDSHIP_API_OAUTH_URL
    value: {{ .Values.hardshipApi.oauthUrl }}
  - name: CCC_API_BASE_URL
    value: {{ .Values.cccApi.baseUrl }}
  - name: CCC_API_OAUTH_URL
    value: {{ .Values.cccApi.oauthUrl }}
  - name: CCP_API_BASE_URL
    value: {{ .Values.ccpApi.baseUrl }}
  - name: CCP_API_OAUTH_URL
    value: {{ .Values.ccpApi.oauthUrl }}
  - name: CMA_API_BASE_URL
    value: {{ .Values.cmaApi.baseUrl }}
  - name: CMA_API_OAUTH_URL
    value: {{ .Values.cmaApi.oauthUrl }}
  - name: VALIDATION_API_BASE_URL
    value: {{ .Values.validationApi.baseUrl }}
  - name: VALIDATION_API_OAUTH_URL
    value: {{ .Values.validationApi.oauthUrl }}
  - name: CAT_API_BASE_URL
    value: {{ .Values.catApi.baseUrl }}
  - name: CAT_API_OAUTH_URL
    value: {{ .Values.catApi.oauthUrl }}
  - name: JWT_ISSUER_URI
    value: {{ .Values.jwt.issuerUri }}
  - name: HARDSHIP_API_OAUTH_CLIENT_ID
    valueFrom:
        secretKeyRef:
            name: hardship-api-oauth-client-id
            key: HARDSHIP_API_OAUTH_CLIENT_ID
  - name: HARDSHIP_API_OAUTH_CLIENT_SECRET
    valueFrom:
        secretKeyRef:
            name: hardship-api-oauth-client-secret
            key: HARDSHIP_API_OAUTH_CLIENT_SECRET
  - name: CCC_API_OAUTH_CLIENT_ID
    valueFrom:
        secretKeyRef:
            name: ccc-api-oauth-client-id
            key: CCC_API_OAUTH_CLIENT_ID
  - name: CCC_API_OAUTH_CLIENT_SECRET
    valueFrom:
        secretKeyRef:
            name: ccc-api-oauth-client-secret
            key: CCC_API_OAUTH_CLIENT_SECRET
  - name: CCP_API_OAUTH_CLIENT_ID
    valueFrom:
        secretKeyRef:
            name: ccp-api-oauth-client-id
            key: CCP_API_OAUTH_CLIENT_ID
  - name: CCP_API_OAUTH_CLIENT_SECRET
    valueFrom:
        secretKeyRef:
            name: ccp-api-oauth-client-secret
            key: CCP_API_OAUTH_CLIENT_SECRET
  - name: CMA_API_OAUTH_CLIENT_ID
    valueFrom:
        secretKeyRef:
            name: cma-api-oauth-client-id
            key: CMA_API_OAUTH_CLIENT_ID
  - name: CMA_API_OAUTH_CLIENT_SECRET
    valueFrom:
        secretKeyRef:
            name: cma-api-oauth-client-secret
            key: CMA_API_OAUTH_CLIENT_SECRET
  - name: VALIDATION_API_OAUTH_CLIENT_ID
    valueFrom:
        secretKeyRef:
            name: validation-api-oauth-client-id
            key: VALIDATION_API_OAUTH_CLIENT_ID
  - name: VALIDATION_API_OAUTH_CLIENT_SECRET
    valueFrom:
        secretKeyRef:
            name: validation-api-oauth-client-secret
            key: VALIDATION_API_OAUTH_CLIENT_SECRET
  - name: MAAT_API_OAUTH_CLIENT_ID
    valueFrom:
        secretKeyRef:
            name: maat-api-oauth-client-id
            key: MAAT_API_OAUTH_CLIENT_ID
  - name: MAAT_API_OAUTH_CLIENT_SECRET
    valueFrom:
        secretKeyRef:
            name: maat-api-oauth-client-secret
            key: MAAT_API_OAUTH_CLIENT_SECRET
  - name: CAT_API_OAUTH_CLIENT_ID
    valueFrom:
        secretKeyRef:
            name: cat-api-oauth-client-id
            key: CAT_API_OAUTH_CLIENT_ID
  - name: CAT_API_OAUTH_CLIENT_SECRET
    valueFrom:
        secretKeyRef:
            name: cat-api-oauth-client-secret
            key: CAT_API_OAUTH_CLIENT_SECRET
  - name: EVIDENCE_API_OAUTH_CLIENT_ID
    valueFrom:
        secretKeyRef:
            name: evidence-api-oauth-client-id
            key: EVIDENCE_API_OAUTH_CLIENT_ID
  - name: EVIDENCE_API_OAUTH_CLIENT_SECRET
    valueFrom:
        secretKeyRef:
            name: evidence-api-oauth-client-secret
            key: EVIDENCE_API_OAUTH_CLIENT_SECRET
{{- end -}}
