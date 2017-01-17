package mil.nga.giat.geowave.datastore.hbase.query;

import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import mil.nga.giat.geowave.core.index.ByteArrayId;
import mil.nga.giat.geowave.core.store.DataStore;
import mil.nga.giat.geowave.core.store.adapter.DataAdapter;
import mil.nga.giat.geowave.core.store.base.DataStoreQuery;
import mil.nga.giat.geowave.core.store.index.PrimaryIndex;
import mil.nga.giat.geowave.datastore.hbase.operations.config.HBaseOptions;

abstract public class HBaseQuery extends
		DataStoreQuery
{
	protected HBaseOptions options = null;

	public HBaseQuery(
			final DataStore dataStore,
			final PrimaryIndex index,
			final String... authorizations ) {
		this(
				dataStore,
				null,
				index,
				null,
				authorizations);
	}

	public HBaseQuery(
			final DataStore dataStore,
			final List<ByteArrayId> adapterIds,
			final PrimaryIndex index,
			final Pair<List<String>, DataAdapter<?>> fieldIds,
			final String... authorizations ) {
		super(
				dataStore,
				adapterIds,
				index,
				fieldIds,
				null,
				authorizations);
	}

	public void setOptions(
			HBaseOptions options ) {
		this.options = options;
	}
}
