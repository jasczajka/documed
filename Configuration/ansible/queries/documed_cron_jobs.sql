DO $$
DECLARE
    target_db text := '%TARGET_DB%';
BEGIN
    -- Schedule OTP cleanup to run daily at midnight (00:00)
    IF EXISTS (SELECT 1 FROM pg_proc WHERE proname = 'cleanup_pending_users') THEN
        PERFORM cron.schedule_in_database(
            target_db || '_cleanup_pending_users_daily',
            '0 0 * * *',
            'SELECT cleanup_pending_users()',
            target_db
        );
    END IF;

    -- Schedule pending users cleanup to run daily at midnight (00:00)
    IF EXISTS (SELECT 1 FROM pg_proc WHERE proname = 'cleanup_otps') THEN
        PERFORM cron.schedule_in_database(
            target_db || '_cleanup_opts_daily',
            '0 0 * * *',
            'SELECT cleanup_otps()',
            target_db
        );
    END IF;

    -- Schedule old timeslots cleanup to run weekly on Sunday at 03:00
    IF EXISTS (SELECT 1 FROM pg_proc WHERE proname = 'cleanup_old_timeslots') THEN
        PERFORM cron.schedule_in_database(
            target_db || '_cleanup_old_timeslots_weekly',
            '0 3 * * 0',
            'SELECT cleanup_old_timeslots()',
            target_db
        );
    END IF;

    -- Schedule old notifications cleanup to run weekly on Sunday at 03:00
    IF EXISTS (SELECT 1 FROM pg_proc WHERE proname = 'cleanup_old_notifications') THEN
        PERFORM cron.schedule_in_database(
            target_db || '_cleanup_old_notifications_weekly',
            '0 3 * * 0',
            'SELECT cleanup_old_notifications()',
            target_db
        );
    END IF;
EXCEPTION WHEN others THEN
    RAISE NOTICE 'Failed to schedule jobs for %: %', target_db, SQLERRM;
END $$;
