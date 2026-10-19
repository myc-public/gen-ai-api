# gen-ai-api

POC d'API d'IA générative en Spring Boot et Spring AI : un même usage (résumé, génération, sortie structurée)
appelé sur plusieurs fournisseurs (OpenAI, Google Gen AI, Hugging Face, Ollama, Docker Model Runner). Parent Maven
`craft-parent` (dépôt `artifacts-manager`).

**Hors de la chaîne déployée** : aucune application `gitops-platform`, aucune exposition par APISIX. Le job
Jenkins existe mais est cassé (dette 24). Vue d'ensemble du socle :
[platform-handbook](https://github.com/myc-public/platform-handbook).

## Démarrer en local

Prérequis : Java 21, Maven avec le `settings.xml` de `artifacts-manager` (Nexus), et une clé ou un modèle local
pour le fournisseur choisi.

```powershell
cd D:\workspace\public\gen-ai-api
$env:OPENAI_API_KEY = '<clé>'          # selon le fournisseur ; jamais commitée
mvn spring-boot:run                    # http://localhost:8081
```

Ollama (modèle local personnalisé) : [docs/ollama.md](docs/ollama.md).

## Endpoints

| Préfixe | Fournisseur | Exemples |
|---|---|---|
| `/api/openai/chat` | OpenAI | `summarize-content`, `summarize-with-stream` (SSE), `summarize-meeting-notes-structured`, `structured-list`, `structured-map` |
| `/api/vertexai/chat` | Google Gen AI | `generate-java-docs` |
| `/api/huggingface/chat` | Hugging Face | `generate-code` |
| `/api/ollama/chat` | Ollama | `draft-email` |
| `/api/docker-model-runner/chat` | Docker Model Runner | `linkedin-post-generator` |

Toutes les opérations sont des `POST`. Fournisseur par défaut : `app.ai.provider` (`application.yaml`).

## Build et tests

```powershell
mvn verify
```

## Configuration

| Variable | Rôle |
|---|---|
| `OPENAI_API_KEY` | clé OpenAI |
| `HUGGINGFACE_API_KEY` | clé Hugging Face |

Google Gen AI utilise les identifiants Google de la machine (projet et région dans `application.yaml`).
Ollama : `http://localhost:11434`.

## CI/CD

- `Jenkinsfile` (racine) : validation Maven par Nexus. Le job Jenkins `gen-ai-api` attend `cicd/Jenkinsfile-ci`,
  absent : à réaligner (dette 24).
- `k8s/` et `scripts/build-and-deploy-k8s.ps1` : déploiement d'essai sur un Kubernetes générique (registry à
  renseigner), hors socle.

## Liens

- Domaine RAG du socle : [roadmap/rag.md](https://github.com/myc-public/platform-handbook/blob/develop/roadmap/rag.md).
- Dettes : [dettes.md](https://github.com/myc-public/platform-handbook/blob/develop/dettes.md) (24, 25).
