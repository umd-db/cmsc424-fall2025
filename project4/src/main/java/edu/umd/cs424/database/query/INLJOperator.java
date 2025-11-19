package edu.umd.cs424.database.query;

import edu.umd.cs424.database.Database;
import edu.umd.cs424.database.DatabaseException;
import edu.umd.cs424.database.databox.DataBox;
import edu.umd.cs424.database.table.Record;
import edu.umd.cs424.database.table.RecordIterator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Performs an equijoin between two relations on leftColumnName and
 * rightColumnName respectively using the Simple Nested Loop Join algorithm.
 */
public class INLJOperator extends JoinOperator {
    private Database.Transaction transaction;
    String tableName;
    String columnName;

    public INLJOperator(QueryOperator leftSource, QueryOperator rightSource, String leftColumnName, String rightColumnName, Database.Transaction transaction, String tableName) throws QueryPlanException, DatabaseException {
        super(leftSource, rightSource, leftColumnName, rightColumnName, transaction, JoinType.INDEX);

        this.transaction = transaction;
        this.tableName = tableName;
        this.columnName = rightColumnName;

        this.stats = this.estimateStats();
        this.cost = this.estimateIOCost();
    }

    public Iterator<Record> iterator() throws QueryPlanException, DatabaseException {
        return new INLJIterator();
    }

    public int estimateIOCost() throws QueryPlanException {
        // ignore this
        return 0;
    }

    /**
     * A record iterator that executes the logic for a simple nested loop join.
     * Note that the left table is the "outer" loop and the right table is the
     * "inner" loop.
     */
    private class INLJIterator extends JoinIterator {
        // Iterator over all the records of the left relation.
        // Think of this like the outer table with a small size.
        private RecordIterator leftSourceIterator;

        // The current record from the left relation
        private Record leftRecord;
        // The next record to return
        private Record nextRecord;

        public INLJIterator() throws QueryPlanException, DatabaseException {
            super();

            this.leftSourceIterator = INLJOperator.this.getRecordIterator(this.getLeftTableName());
            if (this.leftSourceIterator.hasNext()) {
                this.leftRecord = this.leftSourceIterator.next();
            }
        }

        /**
         * Returns the next record that should be yielded from this join,
         * or null if there are no more records to join.
         * <p>
         * NOTE: To implement this, take a look at IndexScanIterator's constructor
         * on how to lookup a key on a table using the transaction.
         */
        private Record fetchNextRecord() throws DatabaseException {
            throw new UnsupportedOperationException("Implement this.");
        }

        @Override
        public boolean hasNext() {
            if (this.nextRecord == null) {
                try {
                    this.nextRecord = fetchNextRecord();
                } catch (DatabaseException e) {
                    // Shouldn't ever happen in our homework.
                }
            }
            return this.nextRecord != null;
        }

        @Override
        public Record next() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            Record nextRecord = this.nextRecord;
            this.nextRecord = null;
            return nextRecord;
        }
    }
}
