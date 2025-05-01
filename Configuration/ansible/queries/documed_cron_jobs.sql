

-- Schedule OTP cleanup to run daily at midnight (00:00)
SELECT cron.schedule(
  'cleanup_pending_users_daily',
  '0 0 * * *',
  $$SELECT cleanup_pending_users();$$
);

-- Schedule pending users cleanup to run daily at midnight (00:00)
SELECT cron.schedule(
  'cleanup_opts_daily',
  '0 0 * * *',
  $$SELECT cleanup_otps();$$
);
