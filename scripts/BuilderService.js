function BuilderService() { }

BuilderService._path = '/dwr';

BuilderService.moveTreeNodes = function(p0,callback) {
    DWREngine._execute(BuilderService._path, 'BuilderService', 'moveTreeNodes', p0, callback);
}