package io.github.mitsumi.solutions.spring.dbunit.postgres.test.loaders;

import com.github.springtestdbunit.dataset.ReplacementDataSetLoader;

@SuppressWarnings("PMD.CommentRequired")
public class ReplacementCsvDataSetLoader extends ReplacementDataSetLoader {
    public ReplacementCsvDataSetLoader() {
        super(new CsvDataSetLoader());
    }
}
