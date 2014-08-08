using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;

namespace System
{
  public static class StringExtensions
  {
    public static bool IsEmpty(this string value)
    {
      return String.IsNullOrEmpty(value);
    }

    public static bool HasValue(this string value)
    {
      return !IsEmpty(value);
    }

    public static string GetValueOrDefault(this string value, string defaultValue)
    {
      return value.HasValue() ? value : defaultValue;
    }

    public static IEnumerable<string> GetLines(this string value, int skip = 0)
    {
      int count = 0;
      using (var reader = new StringReader(value))
      {
        string line;
        while ((line = reader.ReadLine()) != null)
        {
          if (count++ >= skip)
            yield return line;
        }
      }
    }

    public static string Before(this string value, string separator)
    {
      int index = value.IndexOf(separator);

      if (index == -1)
        return value;

      return value.Substring(0, index);
    }

    public static string After(this string value, string separator)
    {
      int index = value.IndexOf(separator);

      if (index == -1 || index == value.Length)
        return String.Empty;

      return value.Substring(index + separator.Length);
    }

    public static string AfterLast(this string value, string separator)
    {
      int index = value.LastIndexOf(separator);

      if (index == -1)
        return value;

      return value.Substring(index + separator.Length);
    }

    public static string BeforeLast(this string value, string separator)
    {
      int index = value.LastIndexOf(separator);

      if (index == -1)
        return String.Empty;

      return value.Substring(0, index);
    }

    public static int AsInt32(this string value)
    {
      if (IsEmpty(value)) return 0;
      return Convert.ToInt32(value);
    }

    public static IEnumerable<string> AsDelimited(this string value)
    {
      return value.Split(new[] {',', ';'}, StringSplitOptions.RemoveEmptyEntries);
    }

    public static string SplitIntoLines(this string value, int charactersPerLine = 64)
    {
      StringBuilder lines = new StringBuilder();

      int index = 0;
      int count = charactersPerLine;

      while ((index + count) < value.Length)
      {
        lines.AppendLine(value.Substring(index, count));
        index += charactersPerLine;
      }

      count = value.Length - index;

      if (count > 0)
      {
        lines.Append(value.Substring(0, count));
      }

      return lines.ToString();
    }
  }
}