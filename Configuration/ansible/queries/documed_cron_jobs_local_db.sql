
-- Note: remember to adjust the database name!

CREATE EXTENSION IF NOT EXISTS pg_cron;
-- Schedule OTP cleanup to run daily at midnight (00:00)
SELECT cron.schedule_in_database(
    'cleanup-otps-daily',
    '0 0 * * *',  
    'SELECT cleanup_otps()',
    'prod_db'
);

-- Schedule pending users cleanup to run daily at midnight (00:00)
SELECT cron.schedule_in_database(
    'cleanup-pending-users-daily',
    '0 0 * * *',  
    'SELECT cleanup_pending_users()',
    'prod_db'
);

-- Schedule old timeslots cleanup to run weekly on Sunday at 03:00
SELECT cron.schedule_in_database(
    'cleanup_old_timeslots_weekly',
    '0 3 * * 0',  
    'SELECT cleanup_old_timeslots()',
    'prod_db'
);
