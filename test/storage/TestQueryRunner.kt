package storage

import com.kamilh.storage.common.QueryRunner
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class TestQueryRunner(override val dispatcher: CoroutineDispatcher = Dispatchers.Unconfined) : QueryRunner {
    override suspend fun <T> run(body: () -> T): T = body()
    override suspend fun <T> runTransaction(body: () -> T): T = body()
}

val testQueryRunner: TestQueryRunner = TestQueryRunner()