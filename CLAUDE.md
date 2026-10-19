# gen-ai-api

POC Spring AI multi-fournisseurs, hors chaîne déployée. Lire d'abord [README.md](README.md). Règles communes du
socle : `platform-handbook`.

## Commandes

- Tests : `mvn verify`.
- Lancer : `mvn spring-boot:run` (port 8081) avec la clé du fournisseur en variable d'environnement.

## Règles propres à ce dépôt

- Aucune clé d'API dans le code, `application.yaml` ou un script : variables d'environnement uniquement.
- Versions de Spring AI : celles du BOM déclaré dans `pom.xml` ; ne pas inventer d'API absente de cette version.
- `logs/` est ignoré : ne jamais versionner de journal d'exécution (dette 25 pour l'existant).
