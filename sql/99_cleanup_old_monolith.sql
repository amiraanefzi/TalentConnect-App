-- =====================================================================
-- ARCHIVE : Ancien monolithe talentconnect-backend
-- Ce script renomme la base en talentconnect_ARCHIVE pour conservation
-- =====================================================================

-- NOTE: MySQL ne supporte pas RENAME DATABASE directement.
-- Option 1: Supprimer (si données inutiles)
-- DROP DATABASE talentconnect;

-- Option 2: Garder en archive (recommandé)
-- La base talentconnect reste mais n'est plus utilisée par aucun service.
-- Tous les services utilisent maintenant leurs propres bases dédiées :
--   - talentconnect_auth   → auth-service       (port 8081)
--   - talentconnect_jobs   → job-service         (port 8085)
--   - candidatures_db      → candidatures-service (port 8084)
--   - talentconnect_files  → file-service         (port 8082)
--   - talent_connect_chatbot → chatbot-service    (port 8083)

-- Pour nettoyer définitivement l'ancienne base :
-- DROP DATABASE IF EXISTS talentconnect;

