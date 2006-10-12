function BuilderService() { }

BuilderService._path = '/dwr';

BuilderService.movePage = function(p0, p1, callback) {
    DWREngine._execute(BuilderService._path, 'BuilderService', 'movePage', p0, p1, callback);
}

BuilderService.changeNodeName = function(p0, p1, callback) {
    DWREngine._execute(BuilderService._path, 'BuilderService', 'changePageName', p0, p1, callback);
}