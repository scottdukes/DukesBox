using System;
using System.IO;
using System.Reactive.Linq;

namespace net.scottdukes.DukesBox
{
  internal class FileSystemObservable
  {
    private readonly FileSystemWatcher mFileSystemWatcher;
    private readonly string mBaseDir;

    public FileSystemObservable( string baseDir,
      string filter,
      bool includeSubdirectories )
    {
      mBaseDir = baseDir;
      mFileSystemWatcher = new FileSystemWatcher( baseDir, filter )
      {
        EnableRaisingEvents = true,
        IncludeSubdirectories = includeSubdirectories
      };

      var createdFiles = Observable.FromEventPattern<FileSystemEventHandler, FileSystemEventArgs>
        ( h => mFileSystemWatcher.Created += h,
          h => mFileSystemWatcher.Created -= h )
        .Select( x => x.EventArgs );

      var changedFiles = Observable.FromEventPattern<FileSystemEventHandler, FileSystemEventArgs>
        (h => mFileSystemWatcher.Changed += h,
          h => mFileSystemWatcher.Changed -= h)
        .Select(x => x.EventArgs);

      //      createdFiles.Merge( changedFiles )
      //        .Select( x => new FileInfo( x.FullPath ) )
      //        .Select( BuildVFile )
      //        .Buffer( TimeSpan.FromSeconds( 5 ) )
      //        .Select( BuildManifest );
      
      CreatedFiles = createdFiles;

      ChangedFiles = changedFiles;

      Errors = Observable.FromEventPattern<ErrorEventHandler, ErrorEventArgs>
        ( h => mFileSystemWatcher.Error += h,
          h => mFileSystemWatcher.Error -= h )
        .Select( x => x.EventArgs );
    }

    //    private Manifest BuildManifest( IList<VFile> arg )
    //    {
    //      throw new NotImplementedException();
    //    }

    public IObservable<ErrorEventArgs> Errors { get; private set; }

    public IObservable<FileSystemEventArgs> CreatedFiles { get; private set; }
    public IObservable<FileSystemEventArgs> ChangedFiles { get; private set; }
  }
}