package io.github.mitsumi.solutions.spring.dbunit.postgres.test.loaders;

import com.github.springtestdbunit.dataset.AbstractDataSetLoader;
import lombok.NoArgsConstructor;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.csv.CsvURLDataSet;
import org.springframework.core.io.Resource;

/**
 * CsvDataSetLoader.
 *
 * @author mitsumi solutions
 */
@NoArgsConstructor
public class CsvDataSetLoader extends AbstractDataSetLoader {

    @Override
    protected IDataSet createDataSet(final Resource resource) throws Exception {
        return new CsvURLDataSet(resource.getURL());
    }

}
