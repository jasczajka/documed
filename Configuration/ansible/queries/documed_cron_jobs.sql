
SELECT cron.schedule(
  'cleanup_pending_users_daily',
  '0 0 * * *',
  $$SELECT cleanup_pending_users();$$
);

SELECT cron.schedule(
  'cleanup_opts_daily',
  '0 0 * * *',
  $$SELECT cleanup_otps();$$
);