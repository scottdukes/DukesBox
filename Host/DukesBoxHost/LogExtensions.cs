// ReSharper disable CheckNamespace
namespace log4net
// ReSharper restore CheckNamespace
{
  using System;
  using Core;

  public static class LogExtensions
  {
    private static readonly Type ThisDeclaringType = typeof(LogExtensions);

    public static void Trace(this ILog log, object message, Exception exception)
    {
      log.Logger.Log(ThisDeclaringType, Level.Trace, message, exception);
    }

    public static void Trace(this ILog log, object message)
    {
      Trace(log, message, null);
    }

    public static void TraceFormat(this ILog log, string format, params object[] args)
    {
      var message = string.Format(format, args);
      Trace(log, message);
    }

    public static void Verbose(this ILog log, object message, Exception exception)
    {
      log.Logger.Log(ThisDeclaringType, Level.Verbose, message, exception);
    }

    public static void Verbose(this ILog log, object message)
    {
      Verbose(log, message, null);
    }

    public static void VerboseFormat(this ILog log, string format, params object[] args)
    {
      var message = string.Format(format, args);
      Verbose(log, message);
    }
  }
}