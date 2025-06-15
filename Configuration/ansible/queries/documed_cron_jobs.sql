
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

-- Schedule old timeslots cleanup to run weekly on Sunday at 03:00
SELECT cron.schedule(
  'cleanup_old_timeslots_weekly',
  '0 3 * * 0',
  $$SELECT cleanup_old_timeslots();$$
);

-- Schedule old notifications cleanup to run weekly on Sunday at 03:00
SELECT cron.schedule(
  'cleanup_old_notifications_weekly',
  '0 3 * * 0',
  $$SELECT cleanup_old_notifications();$$
);
