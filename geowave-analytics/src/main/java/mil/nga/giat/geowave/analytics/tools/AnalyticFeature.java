package mil.nga.giat.geowave.analytics.tools;

import java.util.List;

import mil.nga.giat.geowave.vector.adapter.FeatureDataAdapter;

import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.feature.type.BasicFeatureTypes;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vividsolutions.jts.geom.Geometry;

/**
 * A set of utilities to describe and create a simple feature for use within the
 * set of analytics.
 * 
 */
public class AnalyticFeature
{
	final static Logger LOGGER = LoggerFactory.getLogger(AnalyticFeature.class);

	public static SimpleFeature createGeometryFeature(
			final SimpleFeatureType featureType,
			final String batchId,
			final String dataId,
			final String name,
			final String groupID,
			final double weight,
			final Geometry geometry,
			final String[] extraDimensionNames,
			final double[] extraDimensions,
			final int zoomLevel,
			final int iteration,
			final long count ) {
		assert (extraDimensionNames.length == extraDimensions.length);
		final List<AttributeDescriptor> descriptors = featureType.getAttributeDescriptors();
		final Object[] defaults = new Object[descriptors.size()];
		int p = 0;
		for (final AttributeDescriptor descriptor : descriptors) {
			defaults[p++] = descriptor.getDefaultValue();
		}

		final SimpleFeature newFeature = SimpleFeatureBuilder.build(
				featureType,
				defaults,
				dataId);
		newFeature.setAttribute(
				ClusterFeatureAttribute.NAME.attrName(),
				name);
		newFeature.setAttribute(
				ClusterFeatureAttribute.GROUP_ID.attrName(),
				groupID);
		newFeature.setAttribute(
				ClusterFeatureAttribute.ITERATION.attrName(),
				iteration);
		newFeature.setAttribute(
				ClusterFeatureAttribute.WEIGHT.attrName(),
				weight);
		newFeature.setAttribute(
				ClusterFeatureAttribute.BATCH_ID.attrName(),
				batchId);
		newFeature.setAttribute(
				ClusterFeatureAttribute.COUNT.attrName(),
				count);
		newFeature.setAttribute(
				ClusterFeatureAttribute.GEOMETRY.attrName(),
				geometry);
		newFeature.setAttribute(
				ClusterFeatureAttribute.ZOOM_LEVEL.attrName(),
				zoomLevel);
		int i = 0;
		for (final String dimName : extraDimensionNames) {
			newFeature.setAttribute(
					dimName,
					new Double(
							extraDimensions[i++]));
		}
		return newFeature;
	}

	public static FeatureDataAdapter createGeometryFeatureAdapter(
			final String centroidDataTypeId,
			final String[] extraNumericDimensions,
			final String namespaceURI,
			final String SRID ) {
		try {
			final SimpleFeatureTypeBuilder builder = new SimpleFeatureTypeBuilder();
			builder.setName(centroidDataTypeId);
			builder.setNamespaceURI(namespaceURI == null ?  BasicFeatureTypes.DEFAULT_NAMESPACE : namespaceURI);
			builder.setSRS(SRID);
			for (final ClusterFeatureAttribute attrVal : ClusterFeatureAttribute.values()) {
				builder.add(
						attrVal.name,
						attrVal.type);
			}
			for (final String extraDim : extraNumericDimensions) {
				builder.add(
						extraDim,
						Double.class);
			}
			return new FeatureDataAdapter(
					builder.buildFeatureType());
		}
		catch (final Exception e) {
			LOGGER.warn(
					"Schema Creation Error.  Hint: Check the SRID.",
					e);
		}

		return null;
	}

	public static enum ClusterFeatureAttribute {
		NAME(
				"name",
				String.class),
		GROUP_ID(
				"groupID",
				String.class),
		ITERATION(
				"iteration",
				Integer.class),
		GEOMETRY(
				"geometry",
				Geometry.class),
		WEIGHT(
				"weight",
				Double.class),
		COUNT(
				"count",
				Long.class),
		ZOOM_LEVEL(
				"level",
				Integer.class),
		BATCH_ID(
				"batchID",
				String.class);

		private final String name;
		private final Class<?> type;

		ClusterFeatureAttribute(
				final String name,
				final Class<?> type ) {
			this.name = name;
			this.type = type;
		}

		public String attrName() {
			return name;
		}

		public Class<?> getType() {
			return type;
		}
	}

}