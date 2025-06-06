package io.github.mitsumi.solutions.spring.dbunit.postgres.test.loaders;

import com.github.springtestdbunit.dataset.AbstractDataSetLoader;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.csv.CsvURLDataSet;
import org.springframework.core.io.Resource;

/**
 * Csvからデータセットを生成するローダ実装クラス
 *
 */
public class CsvDataSetLoader extends AbstractDataSetLoader {

    @Override
    protected IDataSet createDataSet(Resource resource) throws Exception {
        return new CsvURLDataSet(resource.getURL());
    }

}
