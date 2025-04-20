-- File: src/main/resources/db/migration/fixCostantsProblem.sql


-- Update loans table
ALTER TABLE loans
    ADD COLUMN IF NOT EXISTS outstanding_balance DOUBLE PRECISION,
    ADD COLUMN IF NOT EXISTS status VARCHAR(255),
    ADD COLUMN IF NOT EXISTS total_paid DOUBLE PRECISION;

-- Set default values for existing rows
UPDATE loans
SET outstanding_balance = principal_amount,
    status = 'CREATED',
    total_paid = 0.0
WHERE outstanding_balance IS NULL
   OR status IS NULL
   OR total_paid IS NULL;

-- Add NOT NULL constraints
ALTER TABLE loans
    ALTER COLUMN outstanding_balance SET NOT NULL,
    ALTER COLUMN status SET NOT NULL,
    ALTER COLUMN total_paid SET NOT NULL;

-- Update repayment_schedules table
ALTER TABLE repayment_schedules
    ADD COLUMN IF NOT EXISTS status VARCHAR(255);

-- Set default value for existing rows
UPDATE repayment_schedules
SET status = 'PENDING'
WHERE status IS NULL;

-- Add NOT NULL constraint
ALTER TABLE repayment_schedules
    ALTER COLUMN status SET NOT NULL;
