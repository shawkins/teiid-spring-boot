/*
 * Copyright 2012-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.teiid.spring.odata;

import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Future;

import org.springframework.transaction.TransactionStatus;
import org.teiid.jdbc.TeiidDriver;
import org.teiid.olingo.service.LocalClient;
import org.teiid.spring.autoconfigure.TeiidServer;

public class SpringClient extends LocalClient {
    private TeiidServer server;
    private TransactionStatus status;

    public SpringClient(String vdbName, String vdbVersion, Properties properties, TeiidServer server,
            Map<Object, Future<Boolean>> loading) {
        super(vdbName, vdbVersion, properties, loading);
        this.server = server;
    }

    @Override
    protected TeiidDriver getDriver() {
        return this.server.getDriver();
    }


    @Override
    public String startTransaction() throws SQLException {
        if (this.server.isUsingPlatformTransactionManager()) {
            status = this.server.getPlatformTransactionManagerAdapter().getOrCreateTransaction(true).status;
            return "anyid";
        }
        return super.startTransaction();
    }

    @Override
    public void commit(String txnId) throws SQLException {
        if (this.server.isUsingPlatformTransactionManager()) {
            this.server.getPlatformTransactionManagerAdapter().commit(status);
        } else {
            super.commit(txnId);
        }
    }

    @Override
    public void rollback(String txnId) throws SQLException {
        if (this.server.isUsingPlatformTransactionManager()) {
            this.server.getPlatformTransactionManagerAdapter().rollback(status);
        } else {
            super.rollback(txnId);
        }
    }
}
