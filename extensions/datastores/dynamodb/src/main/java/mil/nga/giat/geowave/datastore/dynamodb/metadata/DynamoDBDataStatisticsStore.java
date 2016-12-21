package mil.nga.giat.geowave.datastore.dynamodb.metadata;

import java.util.Map;

import org.apache.log4j.Logger;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;

import mil.nga.giat.geowave.core.index.ByteArrayId;
import mil.nga.giat.geowave.core.store.CloseableIterator;
import mil.nga.giat.geowave.core.store.adapter.statistics.DataStatistics;
import mil.nga.giat.geowave.core.store.adapter.statistics.DataStatisticsStore;
import mil.nga.giat.geowave.datastore.dynamodb.DynamoDBOperations;

/**
 * This class will persist Index objects within an DynamoDB table for GeoWave
 * metadata. The adapters will be persisted in an "INDEX" column family.
 *
 * There is an LRU cache associated with it so staying in sync with external
 * updates is not practical - it assumes the objects are not updated often or at
 * all. The objects are stored in their own table.
 *
 **/
public class DynamoDBDataStatisticsStore extends
		AbstractDynamoDBPersistence<DataStatistics<?>> implements
		DataStatisticsStore
{
	private final static Logger LOGGER = Logger.getLogger(
			DynamoDBDataStatisticsStore.class);
	// this is fairly arbitrary at the moment because it is the only custom
	// iterator added
	private static final int STATS_COMBINER_PRIORITY = 10;
	private static final int STATS_MULTI_VISIBILITY_COMBINER_PRIORITY = 15;
	private static final String STATISTICS_COMBINER_NAME = "STATS_COMBINER";
	private static final String STATISTICS_CF = "STATS";

	public DynamoDBDataStatisticsStore(
			final DynamoDBOperations dynamodbOperations ) {
		super(
				dynamodbOperations);
	}

	@Override
	public void incorporateStatistics(
			final DataStatistics<?> statistics ) {
		// because we're using the combiner, we should simply be able to add the
		// object
		addObject(
				statistics);

		// TODO if we do allow caching after we add a statistic to DynamoDB we
		// do need to make sure we update our cache, but for now we aren't using
		// the cache at all

	}

	@Override
	protected void addObjectToCache(
			final ByteArrayId primaryId,
			final ByteArrayId secondaryId,
			final DataStatistics<?> object ) {
		// don't use the cache at all for now

		// TODO consider adding a setting to use the cache for statistics, but
		// because it could change with each new entry, it seems that there
		// could be too much potential for invalid caching if multiple instances
		// of GeoWave are able to connect to the same DynamoDB tables
	}

	@Override
	protected Object getObjectFromCache(
			final ByteArrayId primaryId,
			final ByteArrayId secondaryId ) {
		// don't use the cache at all

		// TODO consider adding a setting to use the cache for statistics, but
		// because it could change with each new entry, it seems that there
		// could be too much potential for invalid caching if multiple instances
		// of GeoWave are able to connect to the same DynamoDB tables
		return null;
	}

	@Override
	protected boolean deleteObjectFromCache(
			final ByteArrayId primaryId,
			final ByteArrayId secondaryId ) {
		// don't use the cache at all

		// TODO consider adding a setting to use the cache for statistics, but
		// because it could change with each new entry, it seems that there
		// could be too much potential for invalid caching if multiple instances
		// of GeoWave are able to connect to the same DynamoDB tables
		return true;
	}

	@Override
	public DataStatistics<?> getDataStatistics(
			final ByteArrayId adapterId,
			final ByteArrayId statisticsId,
			final String... authorizations ) {
		return getObject(
				statisticsId,
				adapterId,
				authorizations);
	}

	@Override
	protected DataStatistics<?> entryToValue(
			final Map<String, AttributeValue> entry ) {
		final DataStatistics<?> stats = super.entryToValue(
				entry);
		if (stats != null) {
			stats.setDataAdapterId(
					getSecondaryId(
							entry));
		}
		return stats;
	}

	@Override
	protected ByteArrayId getPrimaryId(
			final DataStatistics<?> persistedObject ) {
		return persistedObject.getStatisticsId();
	}

	@Override
	protected ByteArrayId getSecondaryId(
			final DataStatistics<?> persistedObject ) {
		return persistedObject.getDataAdapterId();
	}

	@Override
	public void setStatistics(
			final DataStatistics<?> statistics ) {
		removeStatistics(
				statistics.getDataAdapterId(),
				statistics.getStatisticsId());
		addObject(
				statistics);
	}

	@Override
	public CloseableIterator<DataStatistics<?>> getAllDataStatistics(
			final String... authorizations ) {
		return getObjects(
				authorizations);
	}

	@Override
	public boolean removeStatistics(
			final ByteArrayId adapterId,
			final ByteArrayId statisticsId,
			final String... authorizations ) {
		return deleteObject(
				statisticsId,
				adapterId,
				authorizations);
	}

	@Override
	public CloseableIterator<DataStatistics<?>> getDataStatistics(
			final ByteArrayId adapterId,
			final String... authorizations ) {
		return getAllObjectsWithSecondaryId(
				adapterId,
				authorizations);
	}

	@Override
	protected String getPersistenceTypeName() {
		return STATISTICS_CF;
	}

	@Override
	protected byte[] getVisibility(
			final DataStatistics<?> entry ) {
		return entry.getVisibility();
	}

	@Override
	public void removeAllStatistics(
			final ByteArrayId adapterId,
			final String... authorizations ) {
		deleteObjects(
				adapterId,
				authorizations);
	}
}
