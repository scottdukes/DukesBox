using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;

namespace net.scottdukes.DukesBox
{
  /// <summary>
  /// Finds files with a maximum depth limit.
  /// </summary>
  public class FileFinder
  {
    private readonly string rootPath;

    private readonly int maxDepth;

    /// <summary>
    /// Initializes a new instance of the <see cref="FileFinder"/> class.
    /// </summary>
    /// <param name="rootPath">The root path.</param>
    /// <param name="maxDepth">The max depth.</param>
    public FileFinder(string rootPath, int maxDepth)
    {
      if (!Directory.Exists(rootPath))
      {
        throw new DirectoryNotFoundException(string.Format("The directory '{0}' does not exist.", rootPath));
      }

      if (0 > maxDepth || maxDepth > 20)
      {
        throw new ArgumentOutOfRangeException("maxDepth", "Use a value from 0 to 20.");
      }

      this.rootPath = rootPath;
      this.maxDepth = maxDepth;
    }

    public FileFinder(string rootPath) : this(rootPath, 20)
    {
    }

    /// <summary>
    /// Finds the files.
    /// </summary>
    /// <param name="patterns">The search path.</param>
    /// <returns></returns>
    public IEnumerable<string> FindFiles(params string[] patterns)
    {
      const int RootLevel = 0;

      var directoryStack = new Stack<SearchDirectory>();
      directoryStack.Push(new SearchDirectory(this.rootPath, RootLevel));

      while (directoryStack.Count > 0)
      {
        SearchDirectory currentDirectory = directoryStack.Pop();

        // Add directories with in level limit to stack
        if (currentDirectory.Level < this.maxDepth)
        {
          foreach (SearchDirectory directory in currentDirectory.GetDirectories())
          {
            directoryStack.Push(directory);
          }
        }

        // Return files in the current directory match the filter.
        foreach (var file in patterns.SelectMany(currentDirectory.GetFiles))
        {
          yield return file;
        }
      }
    }

    private struct SearchDirectory
    {
      private readonly int level;

      private readonly string directory;

      public SearchDirectory(string path, int level)
      {
        this.directory = path;
        this.level = level;
      }

      internal int Level
      {
        get
        {
          return this.level;
        }
      }

      public IEnumerable<string> GetFiles(string searchPattern)
      {
        return Directory.GetFiles(this.directory, searchPattern);
      }

      public IEnumerable<SearchDirectory> GetDirectories()
      {
        foreach (string path in Directory.GetDirectories(this.directory))
        {
          yield return new SearchDirectory(path, this.level + 1);
        }
      }
    }
  }
}