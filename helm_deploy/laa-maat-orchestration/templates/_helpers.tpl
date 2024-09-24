{{/*
Expand the name of the chart.
*/}}
{{- define "laa-maat-orchestration.name" -}}
{{- default .Chart.Name .Values.nameOverride | trunc 63 | trimSuffix "-" }}
{{- end }}

{{/*
Create a default fully qualified app name.
We truncate at 63 chars because some Kubernetes name fields are limited to this (by the DNS naming spec).
If release name contains chart name it will be used as a full name.
*/}}
{{- define "laa-maat-orchestration.fullname" -}}
{{- if .Values.fullnameOverride }}
{{- .Values.fullnameOverride | trunc 63 | trimSuffix "-" }}
{{- else }}
{{- $name := default .Chart.Name .Values.nameOverride }}
{{- if contains $name .Release.Name }}
{{- .Release.Name | trunc 63 | trimSuffix "-" }}
{{- else }}
{{- printf "%s-%s" .Release.Name $name | trunc 63 | trimSuffix "-" }}
{{- end }}
{{- end }}
{{- end }}

{{/*
Create chart name and version as used by the chart label.
*/}}
{{- define "laa-maat-orchestration.chart" -}}
{{- printf "%s-%s" .Chart.Name .Chart.Version | replace "+" "_" | trunc 63 | trimSuffix "-" }}
{{- end }}

{{/*
Common labels
*/}}
{{- define "laa-maat-orchestration.labels" -}}
helm.sh/chart: {{ include "laa-maat-orchestration.chart" . }}
{{ include "laa-maat-orchestration.selectorLabels" . }}
{{- if .Chart.AppVersion }}
app.kubernetes.io/version: {{ .Chart.AppVersion | quote }}
{{- end }}
app.kubernetes.io/managed-by: {{ .Release.Service }}
{{- end }}

{{/*
Selector labels
*/}}
{{- define "laa-maat-orchestration.selectorLabels" -}}
app.kubernetes.io/name: {{ include "laa-maat-orchestration.name" . }}
app.kubernetes.io/instance: {{ .Release.Name }}
{{- end }}

{{/*
Create the name of the service account to use
*/}}
{{- define "laa-maat-orchestration.serviceAccountName" -}}
{{- if .Values.serviceAccount.create }}
{{- default (include "laa-maat-orchestration.fullname" .) .Values.serviceAccount.name }}
{{- else }}
{{- default "default" .Values.serviceAccount.name }}
{{- end }}
{{- end }}

{{/*
Create ingress configuration
*/}}
{{- define "laa-maat-orchestration.ingress" -}}
{{- $internalAllowlistSourceRange := (lookup "v1" "Secret" .Release.Namespace "ingress-internal-allowlist-source-range").data.INTERNAL_ALLOWLIST_SOURCE_RANGE | b64dec }}
{{- if $internalAllowlistSourceRange }}
  nginx.ingress.kubernetes.io/whitelist-source-range: {{ $internalAllowlistSourceRange }}
  external-dns.alpha.kubernetes.io/set-identifier: {{ include "laa-maat-orchestration.fullname" . }}-{{ $.Values.ingress.environmentName}}-green
{{- end }}
{{- end }}

{{/*
Create external ingress configuration
*/}}
{{- define "laa-maat-orchestration.externalIngress" -}}
{{- $externalAllowlistSourceRange := (lookup "v1" "Secret" .Release.Namespace "ingress-external-allowlist-source-range").data.EXTERNAL_ALLOWLIST_SOURCE_RANGE | b64dec }}
{{- if $externalAllowlistSourceRange }}
  nginx.ingress.kubernetes.io/whitelist-source-range: {{ $externalAllowlistSourceRange }}
  external-dns.alpha.kubernetes.io/set-identifier: {{ include "laa-maat-orchestration.fullname" . }}-external-{{ $.Values.ingress.environmentName}}-green
{{- end }}
{{- end }}
