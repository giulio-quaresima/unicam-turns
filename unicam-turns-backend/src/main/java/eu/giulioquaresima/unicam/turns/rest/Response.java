package eu.giulioquaresima.unicam.turns.rest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;

/**
 * A simple REST response object 
 * inspired by https://stackoverflow.com/q/12806386/853544 
 * 
 * @param <P>
 * 
 * @author Giulio Quaresima (giulio.quaresima--at--gmail.com)
 */
public class Response<P>
{
	private P payload;
	private boolean success;
	private Error error;
	
	public Response()
	{
		super();
	}
	public Response(P payload)
	{
		this();
		this.payload = payload;
		this.success = true;
		this.error = null;
	}
	public Response(P payload, int errorCode, String errorMessage)
	{
		this();
		this.payload = payload;
		this.success = false;
		this.error = new Error(errorCode, errorMessage);
	}

	public static <P> Response<P> of(P payload)
	{
		return new Response<>(payload);
	}
	public static <P> Response<P> of(@Nullable P payload, int errorCode, String errorMessage)
	{
		return new Response<>(payload, errorCode, errorMessage);
	}
	
	public static <P> ResponseEntity<Response<P>> ok(P payload)
	{
		return ResponseEntity.ok(of(payload));
	}

	public static <P> ResponseEntity<Response<P>> ko(@Nullable P payload, HttpStatus httpStatus, String errorMessage)
	{
		return ResponseEntity.status(httpStatus).body(of(payload, httpStatus.value(), errorMessage));
	}

	public P getPayload()
	{
		return payload;
	}
	public void setPayload(P payload)
	{
		this.payload = payload;
	}
	
	public boolean isSuccess()
	{
		return success;
	}
	public void setSuccess(boolean success)
	{
		this.success = success;
	}
	
	public Error getError()
	{
		return error;
	}
	public void setError(Error error)
	{
		this.error = error;
	}
	
	public static class Error
	{
		private int code;
		private String message;
		
		public Error(int code, String message)
		{
			super();
			this.code = code;
			this.message = message;
		}
		public Error()
		{
			this(0, null);
		}

		public int getCode()
		{
			return code;
		}
		public void setCode(int code)
		{
			this.code = code;
		}
		
		public String getMessage()
		{
			return message;
		}
		public void setMessage(String message)
		{
			this.message = message;
		}
	}
	
}
