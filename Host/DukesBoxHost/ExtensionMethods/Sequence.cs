using System;
using System.Collections.Generic;
using System.Linq;

namespace net.scottdukes.DukesBox
{
  public static class Sequence
  {
    public static void ForEach< T >( this IEnumerable<T> source, Action<T> action )
    {
      foreach( var item in source )
      {
        action( item );
      }
    }

    public static IEnumerable<T> WhereNot< T >( this IEnumerable<T> source, Func<T, bool> predicate )
    {
      return source.Where( Predicates.Not( predicate ) );
    }
  }

  public static class Predicates
  {
    public static Func<T, bool> Not< T >( Func<T, bool> predicate )
    {
      return x => !predicate( x );
    }
  }
}