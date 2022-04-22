package eu.giulioquaresima.unicam.turns.domain.entities.converters;

import java.time.ZoneId;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter (autoApply = true)
public class ZoneIdConverter implements AttributeConverter<ZoneId, String>
{

	@Override
	public String convertToDatabaseColumn(ZoneId attribute)
	{
		if (attribute != null)
		{
			return attribute.toString();
		}
		return null;
	}

	@Override
	public ZoneId convertToEntityAttribute(String dbData)
	{
		if (dbData != null)
		{
			return ZoneId.of(dbData);
		}
		return null;
	}

}
