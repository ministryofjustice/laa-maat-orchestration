{{/* vim: set filetype=mustache: */}}
{{/*
Environment variables for service containers
*/}}
{{- define "laa-maat-orchestration.env-vars" }}
env:
  - name: AWS_REGION
    value: {{ .Values.aws_region }}
  - name: SENTRY_DSN
    value: {{ .Values.sentry.dsn }}
  - name: SENTRY_ENV
    value: {{ .Values.java.host_env }}
  - name: SENTRY_SAMPLE_RATE
    value: {{ .Values.sentry.sampleRate | quote }}
  - name: MAAT_API_BASE_URL
    value: {{ .Values.maatApi.baseUrl }}
  - name: MAAT_API_OAUTH_URL
    value: {{ .Values.maatApi.oauthUrl }}
  - name: HARDSHIP_API_BASE_URL
    value: {{ .Values.hardshipApi.baseUrl }}
  - name: HARDSHIP_API_OAUTH_URL
    value: {{ .Values.hardshipApi.oauthUrl }}
  - name: JWT_ISSUER_URI
    value: {{ .Values.jwt.issuerUri }}
{{- end -}}
