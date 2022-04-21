package eu.giulioquaresima.unicam.turns.security;

public enum CRUD implements Permission
{
	CRUD,
	CRU,
	CRD,
	CUD,
	RUD,
	CR,
	CU,
	CD,
	RU,
	RD,
	UD,
	C,
	R,
	U,
	D
	;
	
	@Override
	public boolean satisfies(Object requiredPermission)
	{
		if (requiredPermission != null)
		{
			String requiredPermissionStr;
			if (requiredPermission instanceof String)
			{
				requiredPermissionStr = (String) requiredPermission;
			}
			else
			{
				requiredPermissionStr = requiredPermission.toString();
			}
			
			String givenPermission = name();
			if (givenPermission.length() > requiredPermissionStr.length())
			{
				char[] charArray = requiredPermissionStr.toCharArray();
				for (int index = 0; index < charArray.length; index++)
				{
					if (givenPermission.indexOf(charArray[index]) < 0)
					{
						return false; // A character in requiredPermissionStr is not present in givenPermission
					}
				}
				return true;
			}
		}
		return false;
	}
}
